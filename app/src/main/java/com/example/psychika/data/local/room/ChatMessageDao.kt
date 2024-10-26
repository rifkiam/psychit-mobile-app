package com.example.psychika.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.psychika.data.entity.DailyAveragePrediction

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE date = :date AND userId = :userId")
    fun getAllMessagesByDate(date: String, userId: String): LiveData<List<ChatMessageEntity>>

    @Query("DELETE FROM chat_messages WHERE role = 'loading'")
    suspend fun deleteChatRoleLoading()

    @Query("SELECT date, AVG(predict) AS averagePredict FROM chat_messages WHERE userId = :userId GROUP BY date ORDER BY date DESC")
    fun getAllDateMessages(userId: String): LiveData<List<DailyAveragePrediction>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(message: ChatMessageEntity)
}