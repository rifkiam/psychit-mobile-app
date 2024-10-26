package com.example.psychika.ui.auth.login

import androidx.lifecycle.ViewModel
import com.example.psychika.data.repository.PsychikaRepository

class LoginViewModel(private val repository: PsychikaRepository): ViewModel() {
    fun login(email: String, password: String) =
        repository.loginApi(email, password)

    fun loginWithGoogle(idToken: String) =
        repository.loginWithGoogle(idToken)
}