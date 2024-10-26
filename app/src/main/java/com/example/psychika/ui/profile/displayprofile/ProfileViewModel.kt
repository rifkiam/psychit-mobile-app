package com.example.psychika.ui.profile.displayprofile

import androidx.lifecycle.ViewModel
import com.example.psychika.data.repository.PsychikaRepository

class ProfileViewModel(private val repository: PsychikaRepository): ViewModel() {
    fun getCurrentUserApi(token: String) =
        repository.getCurrentUserApi(token)

    fun getCurrentUserGoogleAuth(userId: String) =
        repository.getCurrentFirebaseUser(userId)
}