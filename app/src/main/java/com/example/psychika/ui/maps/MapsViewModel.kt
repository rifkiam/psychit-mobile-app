package com.example.psychika.ui.maps

import androidx.lifecycle.ViewModel
import com.example.psychika.data.repository.PsychikaRepository

class MapsViewModel(private val repository: PsychikaRepository): ViewModel() {
    fun getMapsNearbyHospital(lat: Double, lng: Double) =
        repository.getMapsNearbyPlaces("hospital", lat, lng, 10000)
}