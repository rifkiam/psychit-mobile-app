package com.example.psychika.ui.chat

import android.content.Context
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
    private var sessionId: String? = null

    private lateinit var allMessages: List<ChatMessage>
    private var isSessionInitialized = false

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

//        val sessionIdMem = getSessionId()
//        Log.d(TAG, "SessionID onCreateView: $sessionIdMem")
//
//        if (!isSessionInitialized || sessionIdMem == null) {
//            Log.d(TAG, "Masuk initiateSession onCreateView: $isSessionInitialized $sessionIdMem")
//            initiateSession()
//            isSessionInitialized = true
//        }
        Log.d(TAG, "SessionID onCreateView: $sessionId $isSessionInitialized")

        viewModel.getAllDateMessages(userId).observe(viewLifecycleOwner) { dailyAveragePredictions ->
            // Extract the averagePredict values
            val averagePredictPercentages = dailyAveragePredictions.map { String.format("%.2f", it.averagePredict * 100) }
            Log.d("DailyPrediction", "Formatted Percentages: $averagePredictPercentages")
            // Log or use the extracted values
        }

        binding.ivSendMessage.setOnClickListener { sendMessage() }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        sessionId = getSessionId()
        Log.d(TAG, "SessionID onResume: $sessionId $isSessionInitialized")

        if (sessionId == null) {
            Log.d(TAG, "Masuk initiateSession onResume: $isSessionInitialized $sessionId")
            initiateSession()
            isSessionInitialized = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearSessionId()
        Log.d(TAG, "SessionID onDestroy: ${getSessionId()} $sessionId")
    }

    private fun initiateSession() {
        viewModel.startNewSession("Bearer $userId", "psychIT", true).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {}
                is Result.Success -> {
                    // Session response is saved in ViewModel; use it later as needed
                    Log.d(TAG, "Session initialized: ${result.data}")
                    sessionId = result.data.sessionId
                    saveSessionId(sessionId!!)
                }
                is Result.Error -> {
                    // Handle errors if the request fails
                    Log.d(TAG, "Session creation err: ${result.error}")
                    showToast(result.error.message)
                }
            }
        }
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
//                        Utils.getCurrentTime()
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
                    val prediction = 1 - cleanPredictionString.toDouble()
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
            val userMessage = ChatMessage(
                "user",
                userInput,
//                Utils.getCurrentTime()
            )

            getPredict(userInput, userMessage)

            val messagesToSend = allMessages.toMutableList()

            messagesToSend.add(userMessage)

            Log.d(TAG, "All messages : $messagesToSend")

            if (getSessionId() == null) {
                initiateSession();
                Log.d(TAG, "Session Created?: ${getSessionId()}")
            }

            viewModel.sendChat("Bearer $userId", ChatMessage("user", userInput), sessionId!!).observe(requireActivity()) { result ->
                Log.d(TAG, "userInput: $userInput")
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            handler.postDelayed({
                                val loadingMessage = ChatMessage("loading", "")
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
//                                Utils.getCurrentTime()
                            )
                            chatAdapter.addChatMessage(assistantMessage)
                            viewModel.saveToLocalDb(listOf(assistantMessage), userId, 0.0)
                        }

                        is Result.Error -> {
                            chatAdapter.removeLoadingMessage()
                            viewModel.deleteChatRoleLoading()

                            Log.e(TAG, "Error Send Chat Fragment level: ${result.error.message}")
                            val errorTimeoutMessage = ChatMessage(
                                "error",
                                getString(R.string.chat_timeout),
//                                Utils.getCurrentTime()
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

    private fun saveSessionId(sessionId: String) {
        val sharedPref = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("session_id", sessionId).apply()
    }

    private fun getSessionId(): String? {
        val sharedPref = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("session_id", null)
    }

    private fun clearSessionId() {
        val sharedPref = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().remove("session_id").apply()
    }

    companion object {
        const val TAG = "ChatFragment"
    }
}