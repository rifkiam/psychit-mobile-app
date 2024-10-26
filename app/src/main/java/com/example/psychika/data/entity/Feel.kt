package com.example.psychika.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Feel (
    var photo: Int = 0,
    var desc: String = "",
    var isSelected: Boolean = false
): Parcelable