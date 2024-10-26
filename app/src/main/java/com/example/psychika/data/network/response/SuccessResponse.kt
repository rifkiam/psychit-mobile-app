package com.example.psychika.data.network.response

import com.google.gson.annotations.SerializedName

data class SuccessResponse(

	@field:SerializedName("success")
	val success: Boolean
)
