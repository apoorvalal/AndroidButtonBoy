package com.example.buttonboy

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface EventDao {
    @Insert
    suspend fun insert(event: Event)

    @Query("SELECT * FROM events_table ORDER BY id DESC")
    fun getAllEvents(): LiveData<List<Event>>

    @Delete
    suspend fun delete(event: Event)
}