package com.example.eventdicoding.viewmodel

import androidx.lifecycle.*
import com.example.eventdicoding.data.database.FavouriteEvent
import com.example.eventdicoding.data.model.EventItem
import com.example.eventdicoding.data.repository.FavouriteRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repository: FavouriteRepository) : ViewModel() {

    // Observasi LiveData dari semua event favorit sebagai EventItem
    val favouriteEvents: LiveData<List<EventItem>> = repository.getAllFavourites()
        .map { favourites -> favourites.map { it.toEventItem() } }
        .asLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }


    // Menambahkan event ke daftar favorit
    fun addFavourite(event: FavouriteEvent) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.addFavourite(event)
            _errorMessage.value = null
        } catch (e: Exception) {
            _errorMessage.value = "Gagal menambahkan ke favorit: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    // Menghapus event dari daftar favorit
    fun removeFavourite(event: FavouriteEvent) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.removeFavourite(event)
            _errorMessage.value = null
        } catch (e: Exception) {
            _errorMessage.value = "Gagal menghapus dari favorit: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
}
