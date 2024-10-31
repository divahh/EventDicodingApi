package com.example.eventdicoding.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object  ApiConfig {
    private const val BASE_URL = "https://event-api.dicoding.dev/"

    fun getServiceApi(): EventApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventApiService::class.java)
    }
}