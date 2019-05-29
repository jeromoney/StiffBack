package com.example.stiffback.treelineDatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

@Database(entities = {TreelineEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract TreelineDao treelineDao();

    public static AppDatabase buildDatabase(final Context context){
        return Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            "treeline-database")
                .allowMainThreadQueries() //TODO -- REMOVE THIS MAIN THREAD QUERY
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            getInstance(context).treelineDao().insertAll(TreelineEntity.populateData());
                                        }
                                    });
                                }
                            })
                            .build();
    }

    public synchronized static AppDatabase getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }
}
