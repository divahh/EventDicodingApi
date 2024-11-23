package com.example.eventdicoding.data.model

import android.os.Parcelable
import com.example.eventdicoding.data.database.FavouriteEvent
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventItem(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val nameEvent: String,
    @SerializedName("imageLogo") val imgEvent: String,
    @SerializedName("ownerName") val ownerEvent: String,
    val beginTime: String,
    val quota: Int,
    @SerializedName("registrants") val registrant: Int,
    val description: String,
    val link: String,
    var isFavourite: Boolean = false
) : Parcelable {

    fun toFavouriteEvent(): FavouriteEvent {
        return FavouriteEvent(
            id = this.id,
            name = this.nameEvent,
            owner = this.ownerEvent,
            beginTime = this.beginTime,
            imageUrl = this.imgEvent
        )
    }
}
