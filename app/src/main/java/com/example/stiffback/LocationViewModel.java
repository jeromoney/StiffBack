package com.example.stiffback;

import android.app.Application;
import android.location.Location;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.stiffback.remoteDataSource.ElevationValue;
import com.example.stiffback.repository.LocationRetriever;
import com.example.stiffback.treelineDatabase.TreelineEntity;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.List;


public class LocationViewModel extends AndroidViewModel {

    private LocationRepository mRepository;

    private MutableLiveData<Location> mLocation;
    private MutableLiveData<CompassCell> mCompass;
    private MutableLiveData<Double> mSlope;
    private MutableLiveData<Double> mAspect;
    private LiveData<List<TreelineEntity>> mTreelineEntities;

    public LocationViewModel(Application application) {
        super(application);
        mRepository = new LocationRepository(application);
        mTreelineEntities = mRepository.getTreelineEntities();
        mLocation = mRepository.getmLocation();
    }


    public final LiveData<List<TreelineEntity>> getmTreelineEntities(){
        if (mTreelineEntities == null){
            mTreelineEntities = new MutableLiveData<>();
        }
        return mTreelineEntities;
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