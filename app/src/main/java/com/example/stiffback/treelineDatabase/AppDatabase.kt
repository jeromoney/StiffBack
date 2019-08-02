package com.example.stiffback.treelineDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

import java.util.concurrent.Executors

@Database(
        entities = [TreelineEntity::class, ElevationEntity::class],
        version = 3
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun treelineDao(): TreelineDao

    companion object {

        @Volatile var INSTANCE: AppDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this){
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java,
                    "treeline-database")
                    .fallbackToDestructiveMigration() // TODO - Yes I am dirty boy
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Executors.newSingleThreadScheduledExecutor().execute { INSTANCE!!.treelineDao().insertAll(*TreelineEntity.populateData()) }
                        }
                    })
                    .build()
        }
    }
}
