package com.example.stiffback.treelineDatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TreelineDao {

    @get:Query("SELECT * FROM treeline")
    val all: LiveData<List<TreelineEntity>>

    @Insert
    fun insertAll(vararg treelineEntities: TreelineEntity)

    // get an elevation by matching lat and lng
    @Query("SELECT * FROM elevationValue WHERE lat = :lat AND lng = :lng")
    fun elevationMatch(lat: Double, lng: Double): List<ElevationEntity>

    @Insert
    fun insertElevation(elevationEntity: ElevationEntity)
}
