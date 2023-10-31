package com.example.android.marsphotos.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [MarsPhoto::class], version = 1, exportSchema = false)
abstract class MarsDatabase : RoomDatabase() {
    abstract val marsPhotoDao: MarsPhotoDao

    companion object {
        private var INSTANCE: MarsDatabase? = null

        fun getInstance(context: Context): MarsDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    MarsDatabase::class.java,
                    "mars_photos_database"
                ).build()
            }
            return INSTANCE as MarsDatabase
        }
    }
}
