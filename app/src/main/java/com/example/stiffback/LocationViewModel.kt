package com.example.stiffback

import android.app.Application
import android.location.Location

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData


class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val mRepository: LocationRepository

    init {
        mRepository = LocationRepository(application)
    }

    fun getmLocationCell(): MutableLiveData<LocationCell> {
        return this.mRepository.getmLocationCell()
    }

    fun getmLocation(): MutableLiveData<Location> {
        return this.mRepository.getmLocation()
    }

    fun pauseLocationUpdate() {
        mRepository.pauseLocationUpdate()
    }

    fun resumeLocationUpdate() {
        mRepository.resumeLocationUpdate()
    }
}