package com.example.android.marsphotos.network.retrofit

import com.example.android.marsphotos.network.model.MarsPhotoDto
import retrofit2.http.GET

interface MarsApiService {
    /**
     * Returns a [List] of [MarsPhotoDto] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "photos" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("photos")
    suspend fun getPhotos(): List<MarsPhotoDto>
}