package com.example.psychika.data.network.retrofit

import com.example.psychika.data.entity.ChatbotRequest
import com.example.psychika.data.network.response.ChatbotResponse
import com.example.psychika.data.network.response.SuccessResponse
import com.example.psychika.data.network.response.TokenResponse
import com.example.psychika.data.network.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface PsychikaApiService {
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("firstName") firstName: String,
        @Field("lastName") lastName: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): TokenResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): TokenResponse

    @GET("auth/me")
    suspend fun getCurrUser(
        @Header("Authorization") token: String
    ): UserResponse

    @FormUrlEncoded
    @PUT("auth/me")
    suspend fun updateCurrUser(
        @Header("Authorization") token: String,
        @Field("firstName") firstName: String,
        @Field("lastName") lastName: String,
        @Field("email") email: String,
    ): SuccessResponse

    @FormUrlEncoded
    @PUT("auth/me/password")
    suspend fun updatePass(
        @Header("Authorization") token: String,
        @Field("current") currPass: String,
        @Field("password") newPass: String
    ): SuccessResponse

    @POST("chat")
    suspend fun sendChat(
        @Header("Authorization") token: String,
        @Body request: ChatbotRequest
    ): ChatbotResponse
}