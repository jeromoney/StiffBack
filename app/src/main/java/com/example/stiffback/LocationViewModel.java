package com.example.stiffback;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.stiffback.treelineDatabase.TreelineEntity;


public class LocationViewModel extends AndroidViewModel {

    private LocationRepository mRepository;

    private MutableLiveData<CompassCell> mCompass;

    public LocationViewModel(Application application) {
        super(application);
        mRepository = new LocationRepository(application);
        mCompass = mRepository.getmCompass();
    }

    public final MutableLiveData<CompassCell> getmCompass(){
        if (mCompass == null){
            mCompass = new MutableLiveData<>();
        }
        return mCompass;
    }
}