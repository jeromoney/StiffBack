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
}
