package com.example.buttonboy

import android.app.Application

class ButtonBoyApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}