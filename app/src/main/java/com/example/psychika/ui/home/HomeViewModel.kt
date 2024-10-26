package com.example.psychika.ui.home

import androidx.lifecycle.ViewModel
import com.example.psychika.data.repository.PsychikaRepository

class HomeViewModel(private val repository: PsychikaRepository): ViewModel() {
    fun getCurrentUserApi(token: String) =
        repository.getCurrentUserApi(token)

    fun getCurrentUserGoogleAuth() =
        repository.getCurrentUserGoogleAuth()

    fun getCurrentFirebaseUser(userId: String) =
        repository.getCurrentFirebaseUser(userId)

    fun registerWithGogleAuth(userId: String, userMap: HashMap<String, String?>) =
        repository.registerWithGoogleAuth(userId, userMap)
}