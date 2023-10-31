package com.example.android.marsphotos.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.android.marsphotos.api.MarsApi
import com.example.android.marsphotos.data.MarsPhoto
import com.example.android.marsphotos.network.OnlineApiService
import android.util.Log
import com.example.android.marsphotos.data.MarsPhotoDao
import com.example.android.marsphotos.extensions.toMarsPhoto
import com.example.android.marsphotos.network.RetrofitProvider

class MarsRepository(private val marsPhotoDao: MarsPhotoDao, private val context: Context) {

    private val service: MarsApi = OnlineApiService()
    private val serviceA = RetrofitProvider()

    private val marsPhotos = mutableListOf<MarsPhoto>()


//    suspend fun getPhotos() = service.getPhotos()

    suspend fun getPhotos() : List<MarsPhoto> {
        if (isConnectedToInternet()) {
            val photosFromApi = serviceA.getService().getPhotos().map {
                it.toMarsPhoto()
            }
            val entities = photosFromApi.map {
                MarsPhoto(
                    id = it.id,
                    it.url
                )
            }
            marsPhotoDao.insertAllImage(entities)
            return photosFromApi
        } else {
            return marsPhotoDao.getAllImages().map {
                MarsPhoto(
                    id = it.id,
                    it.url
                )
            }
        }
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    suspend fun refreshPhotos() {
        val photosFromApi = service.getPhotos()
        marsPhotos.clear()
        marsPhotos.addAll(photosFromApi)
    }


    fun removePhoto(photoId: String) {
        marsPhotos.removeAll { it.id == photoId }
        Log.d("MarsRepository", "removePhoto: $photoId")
    }

    suspend fun deletePhotoR(photo: MarsPhoto) {
        marsPhotoDao.deleteImages(listOf(photo.id))
    }

}
