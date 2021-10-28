package com.example.weatherappretrofit.repository

import com.example.weatherappretrofit.currentweather.model.WeatherModel
import com.example.weatherappretrofit.currentweather.retrofit.Constants
import com.example.weatherappretrofit.currentweather.retrofit.RetrofitInstance

class Repository {

    suspend fun getWeatherData(city: String): WeatherModel {
        return RetrofitInstance.api.getWeatherData(city, Constants.UNITS, Constants.API_ID)
    }
}