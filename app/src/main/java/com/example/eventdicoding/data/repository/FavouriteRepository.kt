package com.example.eventdicoding.data.repository

import com.example.eventdicoding.data.database.FavouriteEvent
import com.example.eventdicoding.data.database.FavouriteEventDao
import kotlinx.coroutines.flow.Flow

class FavouriteRepository(private val dao: FavouriteEventDao) {

    fun getAllFavourites(): Flow<List<FavouriteEvent>> = dao.getAllFavourites()

    suspend fun addFavourite(event: FavouriteEvent) = dao.addFavourite(event)

    suspend fun removeFavourite(event: FavouriteEvent) = dao.removeFavourite(event)

//    suspend fun isFavourite(eventId: Int): Boolean = dao.getFavouriteById(eventId) != null
}
