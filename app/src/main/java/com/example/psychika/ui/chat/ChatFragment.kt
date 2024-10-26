package com.example.psychika.ui.chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.psychika.R
import com.example.psychika.adapter.ChatAdapter
import com.example.psychika.data.entity.ChatMessage
import com.example.psychika.data.local.preference.user.User
import com.example.psychika.data.local.preference.user.UserPreference
import com.example.psychika.data.network.Result
import com.example.psychika.databinding.FragmentChatBinding
import com.example.psychika.ui.ViewModelFactory
import com.example.psychika.utils.Utils

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private val viewModel by viewModels<ChatViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var chatAdapter: ChatAdapter

    private lateinit var userModel: User
    private lateinit var userPreference: UserPreference
    private lateinit var userId: String
    
    private lateinit var allMessages: List<ChatMessage>

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater)

        userPreference = UserPreference(requireContext())
        userModel = userPreference.getUser()
        userId = userModel.id!!

        showChat()

        binding.ivSendMessage.setOnClickListener { sendMessage() }

        return binding.root
    }

    private fun showChat() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        chatAdapter = ChatAdapter(mutableListOf())

        binding.rvChat.apply {
            setLayoutManager(layoutManager)
            adapter = chatAdapter
        }

        val currentDate = Utils.getCurrentDate()
        viewModel.getChatMessageCurrentDate(currentDate, userId)
            .observe(requireActivity()) { message ->
                val noErrorMessages = message.filter { it.role != "error" }
                if (message.isEmpty()) {
                    val defaultBotMessage = ChatMessage(
                        "assistant",
                        getString(R.string.greeting_message),
                        Utils.getCurrentTime()
                    )
                    chatAdapter.addChatMessage(defaultBotMessage)
                    viewModel.saveToLocalDb(listOf(defaultBotMessage), userId, 0.0)
                } else {
                    chatAdapter.updateChatMessages(message)
                    binding.rvChat.smoothScrollToPosition(message.size - 1)
                }
                Log.d(TAG, "Show All Chat Current Date : $message")

                allMessages = noErrorMessages
                Log.d(TAG, "Show All Chat Current Date without Error Message : $noErrorMessages")
            }
    }

    private fun getPredict(message: String, userMessage: ChatMessage) {
        viewModel.getPredict(message).observe(requireActivity()) { result ->
            when (result) {
                is Result.Loading -> {}
                is Result.Success -> {
                    val response = result.data.prediction
                    val cleanPredictionString = response.replace("\"", "")
                    val prediction = cleanPredictionString.toDouble()
                    viewModel.saveToLocalDb(listOf(userMessage), userId, prediction)
                    Log.d(TAG, "Prediction : $prediction")

                    chatAdapter.addChatMessage(userMessage)
                }

                is Result.Error -> {
                    showToast(result.error.message)
                }
            }
        }
    }


    private fun sendMessage() {
        val userInput = binding.etUserInputMessage.text.toString()
        if (userInput.isNotEmpty()) {
            val userMessage = ChatMessage("user", userInput, Utils.getCurrentTime())

            getPredict(userInput, userMessage)

            val messagesToSend = allMessages.toMutableList()

            messagesToSend.add(userMessage)

            Log.d(TAG, "All messages : $messagesToSend")

            viewModel.sendChat("Bearer $userId", messagesToSend).observe(requireActivity()) { result ->
                Log.d(TAG, "userInput: $userInput")
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            handler.postDelayed({
                                val loadingMessage = ChatMessage("loading", "", "")
                                chatAdapter.addChatMessage(loadingMessage)
                                viewModel.saveToLocalDb(listOf(loadingMessage), userId, 0.0)
                            }, 1500)
                        }

                        is Result.Success -> {
                            chatAdapter.removeLoadingMessage()
                            viewModel.deleteChatRoleLoading()

                            val responseAssistant = result.data.messages.filter {
                                it.role == "assistant"
                            }
                            val responseMessage =
                                if (responseAssistant.isNotEmpty()) {
                                    val lastContent = responseAssistant.size
                                    responseAssistant[lastContent - 1].content
                                } else { "" }
                            Log.d(TAG, "Chatbot: $responseAssistant")
                            val assistantMessage = ChatMessage(
                                "assistant",
                                responseMessage,
                                Utils.getCurrentTime()
                            )
                            chatAdapter.addChatMessage(assistantMessage)
                            viewModel.saveToLocalDb(listOf(assistantMessage), userId, 0.0)
                        }

                        is Result.Error -> {
                            chatAdapter.removeLoadingMessage()
                            viewModel.deleteChatRoleLoading()

                            Log.e(TAG, "Error Send Chat : ${result.error.message}")
                            val errorTimeoutMessage = ChatMessage(
                                "error",
                                getString(R.string.chat_timeout),
                                Utils.getCurrentTime()
                            )
                            chatAdapter.addChatMessage(errorTimeoutMessage)
                            viewModel.saveToLocalDb(listOf(errorTimeoutMessage), userId, 0.0)
                        }
                    }
                }
            }

            binding.etUserInputMessage.setText("")
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "ChatFragment"
    }
}