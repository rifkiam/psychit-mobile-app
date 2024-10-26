package com.example.psychika.data.network.retrofit

import com.example.psychika.data.network.response.NearbyPlacesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NearbyPlacesService {
    @GET("json")
    suspend fun getNearbyPlaces(
        @Query("type") type: String,
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("key") apiKey: String
    ): NearbyPlacesResponse
}