package com.example.psychika.data.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true )
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "userId")
    val userId: String,

    @ColumnInfo(name = "role")
    val role: String,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "time")
    val time: String,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "predict")
    val predict: Double
)