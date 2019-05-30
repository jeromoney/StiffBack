package com.example.stiffback.treelineDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TreelineDao {

    @Query("SELECT * FROM treeline")
    LiveData<List<TreelineEntity>> getAll();

    @Insert
    void insertAll(TreelineEntity... treelineEntities);

    // get an elevation by matching lat and lng
    @Query("SELECT * FROM elevationValue WHERE lat = :lat AND lng = :lng")
    List<ElevationEntity> elevationMatch(double lat, double lng);

    @Insert
    void insertElevation(ElevationEntity elevationEntity);
}
