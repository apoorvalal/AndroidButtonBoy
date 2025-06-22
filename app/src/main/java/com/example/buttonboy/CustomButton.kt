package com.example.buttonboy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "buttons_table")
data class CustomButton(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
