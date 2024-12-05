package com.example.myflightloud

import android.app.Application

class SubscribeFlightApplication : Application() {
    val db by lazy { AppDatabase.getInstance(this) }
}