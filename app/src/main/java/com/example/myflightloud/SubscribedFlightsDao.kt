package com.example.myflightloud

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscribedFlightsDao {
    @Query("SELECT * FROM subscribed_flights")
    fun getAll(): List<SubscribedFlight>

    @Insert()
    fun insertAll(vararg flight: SubscribedFlight)

    @Query("DELETE FROM subscribed_flights ")
    fun deleteAll()

    @Delete
    fun delete(flight: SubscribedFlight)
}