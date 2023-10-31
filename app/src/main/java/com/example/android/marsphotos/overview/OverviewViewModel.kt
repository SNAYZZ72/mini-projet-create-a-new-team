package com.example.android.marsphotos.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.data.MarsDatabase
import com.example.android.marsphotos.data.MarsPhoto
import com.example.android.marsphotos.repository.MarsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

enum class MarsApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel(
    private val repository: MarsRepository,
    private val database: MarsDatabase
) : ViewModel() {

    //private val repository = MarsRepository()

    private val _status = MediatorLiveData<MarsApiStatus>()
    val status: LiveData<MarsApiStatus> = _status

    private val photosLiveData: MediatorLiveData<List<MarsPhoto>> = MediatorLiveData()

    val photos: LiveData<List<MarsPhoto>> = photosLiveData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    init {
        viewModelScope.launch {
            try {
                val result = repository.getPhotos()
                photosLiveData.value = result
            } catch (e: Exception) {
                _error.postValue(e.message)
            }

            _status.addSource(photosLiveData) {
                _status.value = if (it.isEmpty()) MarsApiStatus.ERROR else MarsApiStatus.DONE
            }
        }
    }


    private fun getMarsPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            _status.postValue(MarsApiStatus.LOADING)
            try {
                photosLiveData
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    private fun refreshPhotos() {
        //c'est juste pour remettre les données de base de l'api
        viewModelScope.launch {
            try {
                repository.refreshPhotos()
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    fun deletePhoto(photoId: String) {
        viewModelScope.launch {
            repository.deletePhoto(photoId)
        }
        repository.removePhoto(photoId)
        //on actualise la vue pour que la photo disparaisse
        photosLiveData.value = photosLiveData.value?.filter { it.id != photoId }
    }

    fun savePhotoToDatabase(encodedImage: String) {
        viewModelScope.launch {
            repository.savePhotoToDatabase(encodedImage)
        }
    }


    fun getPhotoById(photoId: String?): MarsPhoto? {
        return photosLiveData.value?.find { it.id == photoId }
    }

    fun updatePhoto(marsPhoto: MarsPhoto?) {
        if (marsPhoto != null) {
            repository.updatePhoto(marsPhoto.id, marsPhoto.liked ?: false)
        }
    }
}
