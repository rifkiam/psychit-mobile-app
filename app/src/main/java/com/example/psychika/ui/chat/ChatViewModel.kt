package com.example.psychika.ui.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.auth0.android.jwt.JWT
import com.example.psychika.data.entity.ChatMessage
import com.example.psychika.data.local.room.ChatMessageEntity
import com.example.psychika.data.repository.PsychikaRepository
import com.example.psychika.data.repository.PsychikaRepository.Companion.TAG
import com.example.psychika.utils.Utils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatViewModel(private val repository: PsychikaRepository) : ViewModel() {
    fun getChatMessageCurrentDate(date: String, userId: String) =
        repository.getAllMessagesByDate(date, userId).map { entities ->
            entities.map {
                ChatMessage(it.role, it.message)
            }
        }

    fun sendChat(token: String, messages: ChatMessage, sessionId: String) =
        repository.sendChat(token, messages, sessionId)

    fun saveToLocalDb(messages: List<ChatMessage>, userId: String, predict: Double) {

        val jwt = JWT(userId)
        val extractedId = jwt.getClaim("id").asString()

        if (extractedId.isNullOrEmpty()) {
            Log.e(TAG, "Failed to decode JWT or 'id' claim is missing")
        }

        Log.d(TAG, "Decoded userId: $extractedId")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val currentTime = dateFormat.format(Date())

        messages.forEach { message ->
            val entity = ChatMessageEntity(
                role = message.role,
                userId = extractedId!!,
                message = message.content,
                time = currentTime,
                date = Utils.getCurrentDate(),
                predict = predict,
            )
            repository.insertMessage(entity)
        }
    }

    fun startNewSession(token: String, model: String, stream: Boolean) =
        repository.startNewSession(token, model, stream)

    fun getPredict(text: String) =
        repository.getPredict(text)

    fun deleteChatRoleLoading() =
        repository.deleteChatRoleLoading()
}