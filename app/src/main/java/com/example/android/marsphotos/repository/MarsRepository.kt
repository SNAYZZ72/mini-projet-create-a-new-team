package com.example.android.marsphotos.repository

import com.example.android.marsphotos.api.MarsApi
import com.example.android.marsphotos.data.MarsPhoto
import com.example.android.marsphotos.network.OnlineApiService
import android.util.Log

class MarsRepository {

    private val service: MarsApi = OnlineApiService()

    private val marsPhotos = mutableListOf<MarsPhoto>()


    suspend fun getPhotos() = service.getPhotos()

    suspend fun refreshPhotos() {
        val photosFromApi = service.getPhotos()
        marsPhotos.clear()
        marsPhotos.addAll(photosFromApi)
    }


    fun removePhoto(photoId: String) {
        marsPhotos.removeAll { it.id == photoId }
        Log.d("MarsRepository", "removePhoto: $photoId")
    }

}
