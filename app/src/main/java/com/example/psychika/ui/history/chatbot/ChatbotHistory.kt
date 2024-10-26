package com.example.psychika.ui.history.chatbot

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.psychika.adapter.ChatAdapter
import com.example.psychika.data.local.preference.user.User
import com.example.psychika.data.local.preference.user.UserPreference
import com.example.psychika.databinding.ActivityChatbotHistoryBinding
import com.example.psychika.ui.ViewModelFactory

class ChatbotHistory : AppCompatActivity() {
    private lateinit var binding: ActivityChatbotHistoryBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var date: String
    private val viewModel by viewModels<ChatbotViewModel> {
        ViewModelFactory.getInstance(this@ChatbotHistory)
    }

    private lateinit var userModel: User
    private lateinit var userPreference: UserPreference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotHistoryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        userPreference = UserPreference(this)
        userModel = userPreference.getUser()
        userId = userModel.id!!

        binding.ivBack.setOnClickListener { finish() }

        date = intent.getStringExtra("DATE_CHAT_HISTORY")!!

        showChat()
    }

    private fun showChat() {
        val layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(mutableListOf())

        binding.rvChat.apply {
            setLayoutManager(layoutManager)
            adapter = chatAdapter
        }

        viewModel.getChatMessageCurrentDate(date, userId).observe(this) { messages ->
            chatAdapter.updateChatMessages(messages)
            binding.rvChat.smoothScrollToPosition(messages.size - 1)
        }
    }
}