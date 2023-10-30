package com.example.android.marsphotos.network

import com.example.android.marsphotos.api.MarsApi
import com.example.android.marsphotos.data.MarsPhoto
import com.example.android.marsphotos.extensions.toMarsPhoto

class OnlineApiService : MarsApi {
    private val service = RetrofitProvider()

    override suspend fun getPhotos(): List<MarsPhoto> {
        return service.getService().getPhotos().map {
            it.toMarsPhoto()
        }
    }

}