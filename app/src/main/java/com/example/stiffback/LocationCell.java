package com.example.stiffback;

import android.location.Location;
import android.util.Log;

import com.example.stiffback.remoteDataSource.ElevationValue;
import com.example.stiffback.treelineDatabase.TreelineEntity;


/**
 * LocationCells is a data object that holds elevation values for not only the location but the
 * eight other surrounding cells. (north,northwest,northeast,.. etc) Those 9 elevation values are
 * used to calculate the slope and aspect.
 */
public class LocationCell {
    private String TAG = LocationCell.class.getSimpleName();
    private Location mLocation;
    private TreelineEntity mTreelineEntity;
    private double mSlope;
    private double mAspect;

    protected Double[][] cellArr = new Double[3][3];

    public LocationCell(Location location){
        this.mTreelineEntity = null;
        this.mLocation = location;
        // initialize cell with zeros
        for (int i = 0; i<3; i++) for (int j = 0; j<3; j++){
            cellArr[i][j] = 0.;
        }
    }

    public Location getmLocation(){
        return this.mLocation;
    }

    public void setmTreelineEntity(TreelineEntity treelineEntity){
        this.mTreelineEntity = treelineEntity;
    }

    public void setmSlope(double slope){
        this.mSlope = slope;
    }

    public double getmSlope(){
        return this.mSlope;
    }

    public void setmAspect(double aspect){
        this.mAspect = aspect;
    }

    public double getmAspect(){
        return this.mAspect;
    }


    public TreelineEntity getmTreelineEntity(){
        return this.mTreelineEntity;
    }

    public Double[][] getCellArr(){
        return this.cellArr;
    }

    /**
     * The asycronous USGS api queries return at random times so the class needs to identify the
     * direction and update appropriately.
     * @param elevationQuery
     * @param i
     * @param j
     * @return
     */
    public final LocationCell updateElevationValue(ElevationValue.PointQueryService.ElevationQuery elevationQuery, int i, int j){
        Log.d(TAG, String.format("Lat:%f lon:%f elev:%f", elevationQuery.mLat,elevationQuery.mLng,elevationQuery.mElevation));
        Double elev = elevationQuery.mElevation;
        cellArr[i+1][j+1] = elev;
        return this;
    }
}
