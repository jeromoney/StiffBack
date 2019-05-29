package com.example.stiffback;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.stiffback.remoteDataSource.ElevationRetrofitClientInstance;
import com.example.stiffback.remoteDataSource.ElevationService;
import com.example.stiffback.remoteDataSource.ElevationValue;
import com.example.stiffback.treelineDatabase.AppDatabase;
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
    private List<TreelineEntity> mAllTreelines;
    private MutableLiveData<Location> mLocation;
    private MutableLiveData<TreelineEntity> mNearestTreeline;
    private MutableLiveData<CompassCell> mCompass;


    LocationRepository(Application application) {
        mDb = AppDatabase.getInstance(application);
        mTreelineDao = mDb.treelineDao();
        mAllTreelines = mTreelineDao.getAll(); // TODO -- need to change to ASYNC
        initializeLocation(application);
        mNearestTreeline = new MutableLiveData<>();
        mCompass = new MutableLiveData<>();
    }

    private void initializeLocation(final Application application) {


        // Create the location API
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(application);

        // Create location Requests
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(TimeUnit.SECONDS.toMillis(5));
        locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(5));
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationCallback locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                getmLocation().postValue(location);
                // TODO - retrieve new elevation values
                updateElevation(location);

                // TODO - Find the closest mountain range from the location
                updateMountainRange(location);

            }
        };
        try {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
        catch (SecurityException exception){
            Log.i(TAG, "Need to allow location permissions");
        }


    }


    /**
     * Creates 9 cells and calls USGS elevation service to find elevation value
     */
    private void updateElevation(Location location){
        // This method needs to wait until the location is found and not null to activate.
        if (location == null) {
            Log.i(TAG,"Location is null");
            return;
        }


        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        for (int i = -1; i<2; i++){
            for (int j = -1; j<2; j++){
                Double newLat = lat + i * THIRD_ARC_SECOND;
                Double newLng = lng + j * THIRD_ARC_SECOND;

                Map<String, String> options = new HashMap<>();
                options.put("x",newLng.toString());
                options.put("y",newLat.toString());
                options.put("output","json");
                options.put("units","Feet");

                // Used to pass integers to the onReponse. Is there a better way?
                final int finalI = i;
                final int finalJ = j;
                ElevationService service = ElevationRetrofitClientInstance.getRetrofitInstance().create(ElevationService.class);
                Call<ElevationValue> call = service.getMyLocation(options);
                call.enqueue(new Callback<ElevationValue>() {
                    @Override
                    public void onResponse(Call<ElevationValue> call, Response<ElevationValue> response) {
                        // Get the response object of our query
                        ElevationValue.PointQueryService.ElevationQuery elevationQuery = response.body().mPointQueryService.mElevationQuery;
                        // Pass it to ViewModel who decides where to place it
                        updateElevationValue(elevationQuery, finalI, finalJ);
                    }
                    @Override
                    public void onFailure(Call<ElevationValue> call, Throwable t) {
                        Log.i(TAG, t.getMessage());
                    }
                });
            }
        }
    }

    /**
     * Finds which one of the 9 cells to update
     * @param elevationQuery
     * @param i
     * @param j
     */
    private void updateElevationValue(ElevationValue.PointQueryService.ElevationQuery elevationQuery, int i, int j){
        LiveData<CompassCell> liveData = getmCompass();
        CompassCell newCompass = liveData.getValue();
        if (newCompass == null) newCompass = new CompassCell();
        newCompass.updateElevationValue(elevationQuery,i,j);
        getmCompass().postValue(newCompass);
        // got new elevation values, now calculate slope
        //updateSlope(newCompass.cellArr);
        //updateAspect(newCompass.cellArr);
    }

    private void updateMountainRange(Location location) {
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
        mNearestTreeline.postValue(nearest_entity);
    }

    private void setmLocation(Location location){
        getmLocation().getValue().set(location);
    }

    public List<TreelineEntity> getTreelineEntities() {
        return mAllTreelines;
    }

    public MutableLiveData<Location> getmLocation(){
        if (mLocation == null){
            mLocation = new MutableLiveData<>();
        }
        return mLocation;
    }

    public MutableLiveData<TreelineEntity> getmNearestTreeline(){
        return mNearestTreeline;
    }

    public MutableLiveData<CompassCell> getmCompass(){
        return mCompass;
    }



}
