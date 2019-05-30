package com.example.stiffback;

import android.app.Application;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.stiffback.remoteDataSource.ElevationRetrofitClientInstance;
import com.example.stiffback.remoteDataSource.ElevationService;
import com.example.stiffback.remoteDataSource.ElevationValue;
import com.example.stiffback.treelineDatabase.AppDatabase;
import com.example.stiffback.treelineDatabase.ElevationEntity;
import com.example.stiffback.treelineDatabase.TreelineDao;
import com.example.stiffback.treelineDatabase.TreelineEntity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.stiffback.SlopeUtils.THIRD_ARC_SECOND;

public class LocationRepository {

    private String TAG = this.getClass().getSimpleName();

    private FusedLocationProviderClient mFusedLocationClient;
    private AppDatabase mDb;
    private TreelineDao mTreelineDao;
    private LiveData<List<TreelineEntity>> mAllTreelines;
    private MutableLiveData<LocationCell> mLocationCell;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private MutableLiveData<Location> mLocation;

    LocationRepository(Application application) {
        mDb = AppDatabase.getInstance(application);
        mTreelineDao = mDb.treelineDao();

        // Set up my observer for treelines. This value shouldn't change but needs to be called
        // after the location has been set.
        mAllTreelines = mTreelineDao.getAll();
        final Observer<List<TreelineEntity>> treelineObserver = new Observer<List<TreelineEntity>>() {
            @Override
            public void onChanged(@Nullable final List<TreelineEntity> treelineEntities) {
                // calculate new nearest treeline
                updateMountainRange();
            }
        };
        final Observer<Location> locationObserver = new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                // call elevation queries and new mountain range
                // retrieve new elevation values
                updateElevation(location);

                // Find the closest mountain range from the location
                updateMountainRange();
            }
        };

        getmLocation().observeForever(locationObserver);
        mAllTreelines.observeForever(treelineObserver);
        initializeLocation(application);
    }

    private void initializeLocation(final Application application) {
        // Create the location API
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(application);

        // Create location Requests
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(TimeUnit.SECONDS.toMillis(5));
        mLocationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(5));
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                setLocation(locationResult);
            }
        };
    }

    /**
     * Got a new location, start calculation from fresh
     * @param locationResult
     */
    private void setLocation(LocationResult locationResult){
        Location location = locationResult.getLastLocation();
        // Get the new location. If the location is unchanged or null, do nothing;
        Location oldLocation = getmLocation().getValue();
        if (location == null || location.equals(oldLocation)){
            return;
        }
        // passed test so post value. Other methods will activate on change.
        getmLocation().postValue(location);
        Log.d(TAG, String.format("New Location Lat:%f Lng:%f" , location.getLatitude(), location.getLongitude()));
        // Start a new object from scratch
        LocationCell locationCell = new LocationCell();
        getmLocationCell().postValue(locationCell);
    }


    public void pauseLocationUpdate(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public void resumeLocationUpdate(){
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
        catch (SecurityException exception){
            Log.i(TAG, "Need to allow location permissions");
        }
    }


    /**
     * Creates 9 cells and calls USGS elevation service to find elevation value.
     */
    private void updateElevation(Location location){
        // This method needs to wait until the location is found and not null to activate.
        if (location == null) {
            Log.i(TAG,"Location is null");
            return;
        }


        double lat = SlopeUtils.arc_second_snap(location.getLatitude());
        double lng = SlopeUtils.arc_second_snap(location.getLongitude());
        for (int i = -1; i<2; i++){
            for (int j = -1; j<2; j++){
                final Double newLat = SlopeUtils.arc_second_snap(lat + i * THIRD_ARC_SECOND);
                final Double newLng = SlopeUtils.arc_second_snap(lng + j * THIRD_ARC_SECOND);
                // Used to pass integers to the onReponse. Is there a better way?
                final int finalI = i;
                final int finalJ = j;
                // If new location is in database, just use cached value
                // else continue, but remember to cache value.

                new AsyncTask<Void, Void, Void>(){

                    @Override
                    protected Void doInBackground(Void... voids) {
                        List<ElevationEntity> elevationEntities = mTreelineDao.elevationMatch(newLat,newLng);
                        if (elevationEntities.size() == 0){
                            // didn't find a match so go ahead with lookup
                            Map<String, String> options = new HashMap<>();
                            options.put("x",newLng.toString());
                            options.put("y",newLat.toString());
                            options.put("output","json");
                            options.put("units","Feet");


                            ElevationService service = ElevationRetrofitClientInstance.getRetrofitInstance().create(ElevationService.class);
                            Call<ElevationValue> call = service.getMyLocation(options);

                            call.enqueue(new Callback<ElevationValue>() {
                                @Override
                                public void onResponse(Call<ElevationValue> call, Response<ElevationValue> response) {
                                    // Get the response object of our query
                                    ElevationValue.PointQueryService.ElevationQuery elevationQuery = response.body().mPointQueryService.mElevationQuery;
                                    // Pass it to LocationCell object who decides where to place it
                                    // TODO- Make sure that old requests are canceled and don't replace newer
                                    // responses
                                    double elevation = elevationQuery.mElevation;
                                    updateElevationValue(elevation, finalI, finalJ);
                                }
                                @Override
                                public void onFailure(Call<ElevationValue> call, Throwable t) {
                                    Log.i(TAG, t.getMessage());
                                }
                            });
                        }
                        else if (elevationEntities.size() == 1){
                            // found a single match so just return value
                            double elevation = elevationEntities.get(0).getElevation();
                            updateElevationValue(elevation, finalI, finalJ);
                        }
                        else{
                            // we should never find more than one match
                            Log.e(TAG, "Found more than one elevation match");
                        }
                        return null;
                    }
                }.execute();



            }
        }
    }

    /**
     * Since async network operations are used to query the USGS servers, the elevation values are
     * passed back at random times. The location cell receives the elevation value and figures
     * which of the 9 cells to place the value in.
     *
     * This method also establishes the pattern of checking out a LocationCell object, modifying it,
     * and then returning it back.
     * @param elevation
     * @param i
     * @param j
     */
    private void updateElevationValue(double elevation, int i, int j){
        LocationCell locationCell = getmLocationCell().getValue();
        locationCell.updateElevationValue(elevation,i,j);
        getmLocationCell().postValue(locationCell);
        // got new elevation values, now calculate slope and aspect
        updateSlopeAspect();
    }

    /**
     * This method does not do the calculation of the slope and aspect but instead controls the order of
     * calculation to prevent race conditions.
     */
    private void updateSlopeAspect(){
        Location location = getmLocation().getValue();
        if (location == null) return;
        LocationCell locationCell = getmLocationCell().getValue();
        Double[][] cells = locationCell.getCellArr();
        if (location == null) return;

        double lng = location.getLongitude();
        double slope = SlopeUtils.slope(cells, lng);
        double aspect = SlopeUtils.aspect(cells);
        locationCell.setmSlope(slope);
        locationCell.setmAspect(aspect);
        getmLocationCell().setValue(locationCell);
    }

    /**
     * Finds the nearest mountain range with associated treeline elevation. The app assumes the
     * treeline of the user must be the same. A crude calculation for sure.
     */
    private void updateMountainRange() {
        Location location = getmLocation().getValue();
        if (location == null) return;
        List<TreelineEntity> treelineEntities = getTreelineEntities();
        if (treelineEntities == null) return;
        // Run through list to find minimum distance
        double min_distance = 99999.;
        TreelineEntity nearest_entity = null;
        for (TreelineEntity treelineEntity:treelineEntities){
            double distance = treelineEntity.latlngDistance(location.getLatitude(),location.getLongitude());
            if (distance < min_distance){
                min_distance = distance;
                nearest_entity = treelineEntity;
            }
        }
        LocationCell locationCell = getmLocationCell().getValue();
        if (locationCell == null) return;
        locationCell.setmTreelineEntity(nearest_entity);
        getmLocationCell().postValue(locationCell);
    }

    public List<TreelineEntity> getTreelineEntities() {
        return mAllTreelines.getValue();
    }

    public MutableLiveData<LocationCell> getmLocationCell(){
        if (this.mLocationCell == null) this.mLocationCell = new MutableLiveData<>();
        return mLocationCell;
    }

    public MutableLiveData<Location> getmLocation(){
        if (this.mLocation == null) this.mLocation = new MutableLiveData<>();
        return mLocation;
    }


}
