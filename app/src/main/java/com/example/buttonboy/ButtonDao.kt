package com.example.buttonboy

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ButtonDao {
    @Insert
    suspend fun insert(button: CustomButton)

    @Query("SELECT * FROM buttons_table ORDER BY id ASC")
    fun getAllButtons(): LiveData<List<CustomButton>>
}