package com.example.buttonboy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events_table")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val eventName: String, // NEW FIELD
    val timestamp: String
)
