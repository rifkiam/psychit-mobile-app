package com.example.psychika.data.network.response

import com.google.gson.annotations.SerializedName

data class ChatbotResponse(

	@field:SerializedName("messages")
	val messages: List<MessagesItem>,

	@field:SerializedName("model")
	val model: String
)

data class MessagesItem(

	@field:SerializedName("role")
	val role: String,

	@field:SerializedName("content")
	val content: String
)
