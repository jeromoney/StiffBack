package com.example.stiffback.treelineDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "elevationValue")
class ElevationEntity(@field:ColumnInfo(name = "lat")
                      val lat: Double, @field:ColumnInfo(name = "lng")
                      val lng: Double, @field:ColumnInfo(name = "elevation")
                      val elevation: Double) {
    @PrimaryKey(autoGenerate = true)
    var xid: Int = 0

}
