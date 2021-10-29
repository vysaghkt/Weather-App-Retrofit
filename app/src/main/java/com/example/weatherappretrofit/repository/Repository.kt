package com.example.weatherappretrofit.repository

import com.example.weatherappretrofit.currentweather.model.WeatherModel
import com.example.weatherappretrofit.constants.Constants
import com.example.weatherappretrofit.currentweather.retrofit.RetrofitInstance
import com.example.weatherappretrofit.forecastweather.retrofit.ForecastRetrofitInstance
import com.example.weatherappretrofit.forecastweather.model.ForecastWeatherModel

class Repository {

    suspend fun getWeatherData(city: String): WeatherModel {
        return RetrofitInstance.api.getWeatherData(city, Constants.UNITS, Constants.API_ID)
    }

    suspend fun getForecastData(lat: Double, lon: Double): ForecastWeatherModel {
        return ForecastRetrofitInstance.forecastApi.getForecastData(
            lat,
            lon,
            Constants.UNITS,
            Constants.API_ID,
            Constants.EXCLUDE
        )
    }
}