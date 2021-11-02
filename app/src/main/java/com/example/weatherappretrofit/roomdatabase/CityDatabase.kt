package com.example.weatherappretrofit.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [City::class], version = 1, exportSchema = false)
abstract class CityDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao

    companion object {

        @Volatile
        private var INSTANCE: CityDatabase? = null

        fun getDatabaseInstance(context: Context): CityDatabase {
            val tempInstance = INSTANCE
                ?: synchronized(this) {
                    return Room.databaseBuilder(
                        context.applicationContext,
                        CityDatabase::class.java,
                        "city_name"
                    ).build()
                }
            return tempInstance
        }

    }
}