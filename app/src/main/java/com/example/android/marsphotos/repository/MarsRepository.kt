package com.example.android.marsphotos.repository

import com.example.android.marsphotos.api.MarsApi
import com.example.android.marsphotos.network.OnlineApiService

class MarsRepository {

    private val service: MarsApi = OnlineApiService()
    suspend fun getPhotos() = service.getPhotos()
}
