package com.example.psychika.data.network.response

import com.google.gson.annotations.SerializedName

data class MessageErrorResponse(

	@field:SerializedName("message")
	val message: String
)
