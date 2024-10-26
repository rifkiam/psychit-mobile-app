package com.example.psychika.ui.history.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.psychika.data.entity.ChatMessage
import com.example.psychika.data.repository.PsychikaRepository

class ChatbotViewModel(private val repository: PsychikaRepository): ViewModel() {
    fun getChatMessageCurrentDate(date: String, userId: String) =
        repository.getAllMessagesByDate(date, userId).map { entities ->
            entities.map {
                ChatMessage(it.role, it.message, it.time)
            }
        }
}