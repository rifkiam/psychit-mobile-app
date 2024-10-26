package com.example.psychika.ui.profile.editprofile

import androidx.lifecycle.ViewModel
import com.example.psychika.data.repository.PsychikaRepository

class EditProfileViewModel(private val repository: PsychikaRepository): ViewModel() {
    fun updateCurrentUserApi(
        token: String,
        firstName: String,
        lastName: String,
        email: String,
    ) = repository.updateCurrentUserAPI(token, firstName, lastName, email)

    fun updateCurrentUserGoogle(
        userId: String,
        userMap: HashMap<String, String?>
    ) = repository.updateCurrentUserGoogle(userId, userMap)
}