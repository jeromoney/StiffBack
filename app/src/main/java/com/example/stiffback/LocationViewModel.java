package com.example.stiffback;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.stiffback.remoteDataSource.ElevationValue;
import com.example.stiffback.repository.LocationRetriever;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

import java.util.concurrent.TimeUnit;


public class LocationViewModel extends ViewModel {

    private MutableLiveData<Location> mLocation;
    private MutableLiveData<CompassCell> mCompass;
    private MutableLiveData<Double> mSlope;
    private MutableLiveData<Double> mAspect;

    public final MutableLiveData<Location> getmLocation(){
        if (mLocation == null){
            mLocation = new MutableLiveData<>();
        }
        return mLocation;
    }

    public final MutableLiveData<CompassCell> getmCompass(){
        if (mCompass == null){
            mCompass = new MutableLiveData<>();
        }
        return mCompass;
    }

    public final MutableLiveData<Double> getmSlope(){
        if (mSlope == null){
            mSlope = new MutableLiveData<>();
        }
        return mSlope;
    }

    public final MutableLiveData<Double> getmAspect(){
        if (mAspect == null){
            mAspect = new MutableLiveData<>();
        }
        return mAspect;
    }




    /**
     *
     * Calls the location model to retrieve last known location.
     *
     * @param fusedLocationProviderClient
     */
    public void update(FusedLocationProviderClient fusedLocationProviderClient){
        LocationRetriever.getLastLocation(this , fusedLocationProviderClient);
        // The getElevation call needs to be called after the location is found
    }

    public void updateElevation(){
        LocationRetriever.getElevation(this);
    }

    /**
     * Finds which one of the 9 cells to update
     * @param elevationQuery
     * @param i
     * @param j
     */
    public void updateElevationValue(ElevationValue.PointQueryService.ElevationQuery elevationQuery, int i, int j){
        LiveData<CompassCell> liveData = getmCompass();
        CompassCell newCompass = liveData.getValue();
        if (newCompass == null) newCompass = new CompassCell();
        newCompass.updateElevationValue(elevationQuery,i,j);
        getmCompass().postValue(newCompass);
        // got new elevation values, now calculate slope
        updateSlope(newCompass.cellArr);
        updateAspect(newCompass.cellArr);
    }

    private void updateSlope(Double[][] cells){
        double lng = getmLocation().getValue().getLongitude();
        double slope = SlopeUtils.slope(cells, lng);
        getmSlope().postValue(slope);
    }

    private void updateAspect(Double[][] cells){
        double aspect = SlopeUtils.aspect(cells);
        getmAspect().postValue(aspect);
    }
}