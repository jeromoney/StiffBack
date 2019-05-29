package com.example.stiffback;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.stiffback.treelineDatabase.AppDatabase;
import com.example.stiffback.treelineDatabase.TreelineDao;
import com.example.stiffback.treelineDatabase.TreelineEntity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LocationRepository {

    private String TAG = this.getClass().getSimpleName();

    private FusedLocationProviderClient mFusedLocationClient;
    private AppDatabase mDb;
    private TreelineDao mTreelineDao;
    private List<TreelineEntity> mAllTreelines;
    private MutableLiveData<Location> mLocation;
    private MutableLiveData<TreelineEntity> mNearestTreeline;

    LocationRepository(Application application) {
        mDb = AppDatabase.getInstance(application);
        mTreelineDao = mDb.treelineDao();
        mAllTreelines = mTreelineDao.getAll(); // TODO -- need to change to ASYNC
        initializeLocation(application);
        mNearestTreeline = new MutableLiveData<>();
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



}
