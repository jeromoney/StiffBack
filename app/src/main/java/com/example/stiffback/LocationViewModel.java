package com.example.stiffback;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


public class LocationViewModel extends AndroidViewModel {

    private LocationRepository mRepository;

    private MutableLiveData<LocationCell> mCompass;

    public LocationViewModel(Application application) {
        super(application);
        mRepository = new LocationRepository(application);
        mCompass = mRepository.getmCompass();
    }

    public final MutableLiveData<LocationCell> getmCompass(){
        if (mCompass == null){
            mCompass = new MutableLiveData<>();
        }
        return mCompass;
    }
}