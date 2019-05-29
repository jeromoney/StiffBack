package com.example.stiffback;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.stiffback.treelineDatabase.AppDatabase;
import com.example.stiffback.treelineDatabase.TreelineDao;
import com.example.stiffback.treelineDatabase.TreelineEntity;

import java.util.List;

public class LocationRepository {

    private TreelineDao mTreelineDao;
    private  LiveData<List<TreelineEntity>> mAllTreelines;

    LocationRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mTreelineDao = db.treelineDao();
        mAllTreelines = mTreelineDao.getAll();
    }


    public LiveData<List<TreelineEntity>> getTreelineEntities() {
        return mAllTreelines;
    }


}
