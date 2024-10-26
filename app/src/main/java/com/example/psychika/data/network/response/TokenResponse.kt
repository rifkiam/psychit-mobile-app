package com.example.psychika.data.network.response

import com.google.gson.annotations.SerializedName

data class TokenResponse(

	@field:SerializedName("token")
	val token: String,

	@field:SerializedName("refreshToken")
	val refreshToken: String
)
