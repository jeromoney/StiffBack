package com.example.stiffback;

import android.app.Application;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.stiffback.repository.LocationRetriever;
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

    private FusedLocationProviderClient mFusedLocationClient;

    private TreelineDao mTreelineDao;
    private LiveData<List<TreelineEntity>> mAllTreelines;
    private MutableLiveData<Location> mLocation;

    LocationRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mTreelineDao = db.treelineDao();
        mAllTreelines = mTreelineDao.getAll();
        initializeLocation(application);
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
                getmLocation().setValue(location);
            }
        };
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);



    }

    private void setmLocation(Location location){
        getmLocation().getValue().set(location);
    }

    public LiveData<List<TreelineEntity>> getTreelineEntities() {
        return mAllTreelines;
    }

    public MutableLiveData<Location> getmLocation(){
        if (mLocation == null){
            mLocation = new MutableLiveData<>();
        }
        return mLocation;
    }




}
