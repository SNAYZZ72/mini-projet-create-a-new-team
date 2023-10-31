package com.example.android.marsphotos.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.marsphotos.data.MarsDatabase
import com.example.android.marsphotos.repository.MarsRepository

class OverviewViewModelFactory(private val repository: MarsRepository, private val database: MarsDatabase) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverviewViewModel::class.java)){
            return OverviewViewModel(repository, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}