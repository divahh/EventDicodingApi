package com.example.eventdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventdicoding.data.model.EventItem
import com.example.eventdicoding.data.repository.EventRepository
import kotlinx.coroutines.launch

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    // LiveData untuk daftar event yang akan datang
    private val _upcomingEvents = MutableLiveData<List<EventItem>>()
    val upcomingEvents: LiveData<List<EventItem>> get() = _upcomingEvents

    // LiveData untuk daftar event yang sudah selesai
    private val _finishedEvents = MutableLiveData<List<EventItem>>()
    val finishedEvents: LiveData<List<EventItem>> get() = _finishedEvents

    // LiveData untuk hasil pencarian event
    private val _searchResults = MutableLiveData<List<EventItem>>()
    val searchResults: LiveData<List<EventItem>> get() = _searchResults

    // LiveData untuk detail sebuah event
    private val _eventDetail = MutableLiveData<EventItem?>()
    val eventDetail: LiveData<EventItem?> get() = _eventDetail

    // LiveData untuk pesan error
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // LiveData untuk status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Memuat daftar event mendatang (akan datang)
    fun loadUpcomingEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getEvents(active = 1)  // Load upcoming events
                _upcomingEvents.value = response.listEvents
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load upcoming events: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Memuat daftar event yang sudah selesai
    fun loadFinishedEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getEvents(active = 0)  // Load finished events
                _finishedEvents.value = response.listEvents
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load finished events: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Memuat detail sebuah event berdasarkan ID
    fun loadEventDetail(eventId: Int?) {
        if (eventId == null || eventId <= 0) {
            _errorMessage.value = "Invalid event ID"
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val eventDetail = repository.getDetailEvents(eventId) // Dapatkan detail event berdasarkan ID
                _eventDetail.value = eventDetail
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load event detail: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Mencari event yang sudah selesai berdasarkan kata kunci
    fun searchFinishedEvents(keyword: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.searchEvents(active = 0, query = keyword)
                _searchResults.value = response.listEvents
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to search events: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
