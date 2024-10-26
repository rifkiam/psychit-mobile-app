package com.example.psychika.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article (
    val profilePic: Int,
    val publisher: String,
    val title: String,
    val date: String,
    val photo: Int,
    val desc: String
): Parcelable