package com.example.eventdicoding.data.api

import com.example.eventdicoding.data.model.EventDetailResponse
import com.example.eventdicoding.data.model.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EventApiService {
    @GET("events")
    suspend fun getEvents(@Query("active") active: Int): EventResponse

    @GET("events/{id}")
    suspend fun getDetailEvents(@Path("id") id: Int): EventDetailResponse

    @GET("events")
    suspend fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") query: String
    ): EventResponse

    @GET("events")
    suspend fun getReminderEvents(
        @Query("active") active: Int = -1,
        @Query("limit") limit: Int = 1
    ): EventResponse
}