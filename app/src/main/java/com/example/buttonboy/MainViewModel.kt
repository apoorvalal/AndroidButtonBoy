package com.example.buttonboy

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//class MainViewModel(private val eventDao: EventDao) : ViewModel() {
class MainViewModel(private val eventDao: EventDao, private val buttonDao: ButtonDao) : ViewModel() {


    val allEvents: LiveData<List<Event>> = eventDao.getAllEvents()
    val allButtons: LiveData<List<CustomButton>> = buttonDao.getAllButtons() // New property


    fun logEvent(name: String) { // Now accepts a name
    viewModelScope.launch {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTimestamp = sdf.format(Date())
        eventDao.insert(Event(eventName = name, timestamp = currentTimestamp)) // Pass the name
    }
}

    fun deleteEvent(event: Event) = viewModelScope.launch {
        eventDao.delete(event)
    }

    fun insertEvent(event: Event) = viewModelScope.launch {
        eventDao.insert(event)
    }

    fun addButton(name: String) = viewModelScope.launch {
        buttonDao.insert(CustomButton(name = name))
    }
}

// This "Factory" is boilerplate code to allow us to pass the DAO into our ViewModel
class MainViewModelFactory(
    private val eventDao: EventDao,
    private val buttonDao: ButtonDao
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // return MainViewModel(eventDao) as T
            return MainViewModel(eventDao, buttonDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
