package com.example.eventdicoding.data.repository

import android.util.Log
import com.example.eventdicoding.data.api.EventApiService
import com.example.eventdicoding.data.model.EventItem
import com.example.eventdicoding.data.model.EventResponse


class EventRepository(private val apiService: EventApiService) {
    suspend fun getEvents(active: Int): EventResponse {
        val startTime = System.currentTimeMillis()
        val response = apiService.getEvents(active)
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        Log.d("EventRepository", "getEvents($active) took $duration ms, size: ${response.listEvents.size}")
        return response
    }

    suspend fun getDetailEvents(eventId: Int): EventItem {
        return apiService.getDetailEvents(eventId).event
    }

    suspend fun searchEvents(active: Int = -1, query: String): EventResponse {
        return apiService.searchEvents(active, query)
    }

    suspend fun getReminderEvents(active: Int = -1, limit: Int): EventResponse {
        return apiService.getReminderEvents(active, limit)
    }
}