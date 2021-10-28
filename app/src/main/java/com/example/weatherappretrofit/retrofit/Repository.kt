package com.example.weatherappretrofit.retrofit

import com.example.weatherappretrofit.model.WeatherModel

class Repository {

    suspend fun getWeatherData(city: String): WeatherModel{
        return RetrofitInstance.api.getWeatherData(city, Constants.UNITS, Constants.API_ID)
    }
}