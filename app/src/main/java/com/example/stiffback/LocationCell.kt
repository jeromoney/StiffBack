package com.example.stiffback

import android.location.Location
import android.util.Log

import com.example.stiffback.remoteDataSource.ElevationValue
import com.example.stiffback.treelineDatabase.TreelineEntity


/**
 * LocationCells is a data object that holds elevation values for not only the location but the
 * eight other surrounding cells. (north,northwest,northeast,.. etc) Those 9 elevation values are
 * used to calculate the slope and aspect.
 */
class LocationCell {
    val mLocation: Location? = null
    var mTreelineEntity: TreelineEntity? = null
    var mSlope: Double = 0.toDouble()
    var mAspect: Double = 0.toDouble()

    var cellArr = arrayOf(DoubleArray(3),DoubleArray(3),DoubleArray(3)) // Easier way to do this?

    /**
     * The asycronous USGS api queries return at random times so the class needs to identify the
     * direction and update appropriately.
     * @param elevation
     * @param i
     * @param j
     * @return
     */
    fun updateElevationValue(elevation: Double, i: Int, j: Int): LocationCell {
        cellArr[i + 1][j + 1] = elevation
        return this
    }
}

