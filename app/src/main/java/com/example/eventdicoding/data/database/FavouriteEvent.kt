package com.example.eventdicoding.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.eventdicoding.data.model.EventItem

@Entity(tableName = "favourite_events")
data class FavouriteEvent(
    @PrimaryKey val id: Int,
    val name: String,
    val owner: String,
    val beginTime: String,
    val imageUrl: String
) {
    fun toEventItem(): EventItem {
        return EventItem(
            id = id,
            nameEvent = name,
            imgEvent = imageUrl,
            ownerEvent = owner,
            beginTime = beginTime,
            quota = 0,
            registrant = 0,
            description = "",
            link = "",
            isFavourite = true
        )
    }
}
