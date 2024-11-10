package com.example.psychika.data.network.response

import com.google.gson.annotations.SerializedName

data class StartSessionResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("sessionId")
	val sessionId: String
)
