package com.example.eventdicoding.data.model

import com.google.gson.annotations.SerializedName

data class EventDetailResponse(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("event") val event: EventItem
)
