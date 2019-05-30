package com.example.stiffback;

import android.location.Location;
import android.util.Log;

import com.example.stiffback.remoteDataSource.ElevationValue;
import com.example.stiffback.treelineDatabase.TreelineEntity;


/**
 * Contains the 9 cells (north,northwest,northeast,.. etc) and their corresponding elevations.
 */
public class CompassCell {
    private String TAG = CompassCell.class.getSimpleName();
    private Location mLocation;
    private TreelineEntity mTreelineEntity;

    protected Double[][] cellArr = new Double[3][3];

    public CompassCell(Location location){
        this.mTreelineEntity = null;
        this.mLocation = location;
        for (int i = 0; i<3; i++) for (int j = 0; j<3; j++){
            cellArr[i][j] = 0.;
        }
    }

    public void setmLocation(Location location){
        this.mLocation = location;
    }

    public Location getmLocation(){
        return this.mLocation;
    }

    public void setmTreelineEntity(TreelineEntity treelineEntity){
        this.mTreelineEntity = treelineEntity;
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
    public final CompassCell updateElevationValue(ElevationValue.PointQueryService.ElevationQuery elevationQuery, int i, int j){
        Log.d(TAG, String.format("Lat:%f lon:%f elev:%f", elevationQuery.mLat,elevationQuery.mLng,elevationQuery.mElevation));
        Double elev = elevationQuery.mElevation;
        cellArr[i+1][j+1] = elev;
        return this;
    }
}

