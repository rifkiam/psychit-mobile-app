package com.example.psychika.data.network.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserGoogleAuth(
    val id: String? = null,
    val profilePic: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null
): Parcelable
