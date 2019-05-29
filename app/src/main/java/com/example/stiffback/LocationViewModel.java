package com.example.stiffback;

import android.app.Application;
import android.location.Location;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.stiffback.treelineDatabase.TreelineEntity;


public class LocationViewModel extends AndroidViewModel {

    private LocationRepository mRepository;

    private MutableLiveData<Location> mLocation;
    private MutableLiveData<CompassCell> mCompass;
    private MutableLiveData<Double> mSlope;
    private MutableLiveData<Double> mAspect;
    private LiveData<TreelineEntity> mNearestTreeline;

    public LocationViewModel(Application application) {
        super(application);
        mRepository = new LocationRepository(application);
        mLocation = mRepository.getmLocation();
        mNearestTreeline = mRepository.getmNearestTreeline();
        mCompass = mRepository.getmCompass();
    }


    public final LiveData<TreelineEntity> getmNearestTreeline(){
        if (mNearestTreeline == null){
            mNearestTreeline = new MutableLiveData<>();
        }
        return mNearestTreeline;
    }

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