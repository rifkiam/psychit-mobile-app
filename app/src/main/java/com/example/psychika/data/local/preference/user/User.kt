package com.example.psychika.data.local.preference.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String? = "",
    var rememberMe: Boolean = false,
    var googleAuth: Boolean = false
): Parcelable