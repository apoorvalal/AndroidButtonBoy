package com.example.buttonboy

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EventDao {
    @Insert
    suspend fun insert(event: Event)

    @Query("SELECT * FROM events_table ORDER BY id DESC")
    fun getAllEvents(): LiveData<List<Event>>
}