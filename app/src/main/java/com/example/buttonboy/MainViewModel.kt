package com.example.buttonboy

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(private val eventDao: EventDao) : ViewModel() {

    val allEvents: LiveData<List<Event>> = eventDao.getAllEvents()

    fun logEvent() {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentTimestamp = sdf.format(Date())
            eventDao.insert(Event(timestamp = currentTimestamp))
        }
    }

    fun deleteEvent(event: Event) = viewModelScope.launch {
        eventDao.delete(event)
    }

    // We need this for the "Undo" action
    fun insertEvent(event: Event) = viewModelScope.launch {
        eventDao.insert(event)
    }
}

// This "Factory" is boilerplate code to allow us to pass the DAO into our ViewModel
class MainViewModelFactory(private val eventDao: EventDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(eventDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}