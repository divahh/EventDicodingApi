package com.example.eventdicoding.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavourite(event: FavouriteEvent)

    @Delete
    suspend fun removeFavourite(event: FavouriteEvent)

    @Query("SELECT * FROM favourite_events")
    fun getAllFavourites(): Flow<List<FavouriteEvent>>

    @Query("SELECT * FROM favourite_events WHERE id = :eventId")
    suspend fun getFavouriteById(eventId: Int): FavouriteEvent?
}