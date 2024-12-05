package com.example.myflightloud

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscribed_flights")
data class SubscribedFlight(
    @PrimaryKey val flightKey: String,
    @ColumnInfo(name = "bid") var bid: Double?,
    @ColumnInfo(name = "flightPrice") val flightPrice: Double?,
   // @ColumnInfo(name = "isSubscribed") val isSubscribed: Boolean?
)