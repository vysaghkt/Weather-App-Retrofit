package com.example.weatherappretrofit.roomdatabase

import androidx.lifecycle.LiveData

class CityRepository(private val cityDao: CityDao) {

    val getAllData: LiveData<List<City>> = cityDao.getAllCity()

    suspend fun addCity(city: City) {
        cityDao.addCity(city)
    }

    suspend fun deleteCity(city: City){
        cityDao.deleteCity(city)
    }
}