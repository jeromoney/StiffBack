package com.example.stiffback.treelineDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * A sqlite table to store the values of various treeline data around US. At start, the app finds
 * the user's closest treeline entry and uses the value as the treeline. There are probably more
 * refined ways to guess the user's treeline value.
 *
 * Based on wiki article https://en.wikipedia.org/wiki/Tree_line
 */
@Entity(tableName = "treeline")
public class TreelineEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "mountain_range")
    private String mountainRange;

    @ColumnInfo(name = "treeline_elevation")
    private int treelineElevation; // in meters

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "mountain")
    private String mountain; // The peak that the location is based on. Usually the highest in the range

    @ColumnInfo(name = "lat")
    private double lat;

    @ColumnInfo(name = "lng")
    private double lng;
}
