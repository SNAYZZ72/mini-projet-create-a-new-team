package com.example.android.marsphotos.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MarsPhotoDao {
    @Query("SELECT * FROM mars_photos")
    suspend fun getAllImages(): List<MarsPhoto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllImage(images: List<MarsPhoto>)

    @Delete
    suspend fun deleteImage(photo: MarsPhoto)

    @Query("DELETE FROM mars_photos WHERE id In (:ids)")
    suspend fun deleteImages(ids: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarsPhoto(marsPhoto: MarsPhoto)
}