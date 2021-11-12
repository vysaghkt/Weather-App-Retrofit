package com.example.weatherappretrofit.roomdatabase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCity(city: City)

    @Query("SELECT * FROM city_table")
    fun getAllCity(): LiveData<List<City>>

    @Delete
    suspend fun deleteCity(city: City)
}