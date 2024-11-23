package com.example.eventdicoding.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eventdicoding.data.database.AppDatabase
import com.example.eventdicoding.data.repository.FavouriteRepository
import com.example.eventdicoding.viewmodel.FavouriteViewModel

object Injection {

    private fun provideFavouriteRepository(context: Context): FavouriteRepository {
        val database = AppDatabase.getDatabase(context)
        return FavouriteRepository(database.favouriteEventDao())
    }

    fun provideFavoriteViewModelFactory(context: Context): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
                    return FavouriteViewModel(provideFavouriteRepository(context)) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
