package com.example.myflightloud

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [SubscribedFlight::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase(){
    abstract fun subscribedFlightDAO(): SubscribedFlightsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it}
            }

        private fun buildDatabase(context: Context): AppDatabase {

            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "subscribedFlights-db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}