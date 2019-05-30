package com.example.stiffback;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.stiffback.treelineDatabase.TreelineEntity;


public class LocationViewModel extends AndroidViewModel {

    private LocationRepository mRepository;

    private MutableLiveData<CompassCell> mCompass;
    private MutableLiveData<Double> mSlope;
    private MutableLiveData<Double> mAspect;

    public LocationViewModel(Application application) {
        super(application);
        mRepository = new LocationRepository(application);
        mCompass = mRepository.getmCompass();
        mSlope = mRepository.getmSlope();
        mAspect = mRepository.getmAspect();
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

}