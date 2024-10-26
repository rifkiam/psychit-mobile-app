package com.example.psychika.data.network.retrofit

import com.example.psychika.data.network.response.PredictResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ClassificationApiService {
    @GET("predict")
    suspend fun getPredict(
        @Query("text") text: String
    ): PredictResponse
}