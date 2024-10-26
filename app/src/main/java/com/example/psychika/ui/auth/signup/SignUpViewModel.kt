package com.example.psychika.ui.auth.signup

import androidx.lifecycle.ViewModel
import com.example.psychika.data.repository.PsychikaRepository

class SignUpViewModel(private val repository: PsychikaRepository) : ViewModel() {
    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) = repository.registerApi(firstName, lastName, email, password)
}