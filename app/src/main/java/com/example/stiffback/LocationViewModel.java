package com.example.stiffback;

import android.app.Application;
import android.location.Location;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


public class LocationViewModel extends AndroidViewModel {

    private LocationRepository mRepository;
    
    public LocationViewModel(Application application) {
        super(application);
        mRepository = new LocationRepository(application);
    }

    public final MutableLiveData<LocationCell> getmCompass(){
        return this.mRepository.getmLocationCell();
    }

    public final MutableLiveData<Location> getmLocation(){
        return this.mRepository.getmLocation();
    }

    public void pauseLocationUpdate(){
        mRepository.pauseLocationUpdate();
    }

    public void resumeLocationUpdate(){
        mRepository.resumeLocationUpdate();
    }
}