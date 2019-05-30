package com.example.stiffback.treelineDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "elevationValue")
public class ElevationEntity {
    @PrimaryKey(autoGenerate = true)
    private int xid;

    @ColumnInfo(name = "lat")
    private double lat;

    @ColumnInfo(name = "lng")
    private double lng;

    @ColumnInfo(name = "elevation")
    private double elevation;

    public ElevationEntity(double lat, double lng, double elevation) {
        this.lat = lat;
        this.lng = lng;
        this.elevation = elevation;
    }

    public void setXid(int id) {
        this.xid = id;
    }

    public int getXid() {
        return this.xid;
    }
    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }

    public double getElevation(){
        return this.elevation;
    }

}
