package com.example.weatherappretrofit.repository

import com.example.weatherappretrofit.model.WeatherModel
import com.example.weatherappretrofit.constants.Constants
import com.example.weatherappretrofit.retrofit.RetrofitInstance
import com.example.weatherappretrofit.forecastweather.model.ForecastWeatherModel
import retrofit2.Response

class Repository {

    suspend fun getWeatherData(city: String): Response<WeatherModel> {
        return RetrofitInstance.api.getWeatherData(city, Constants.UNITS, Constants.API_ID)
    }

    suspend fun getForecastData(
        lat: Double,
        lon: Double,
        unit: String
    ): Response<ForecastWeatherModel> {
        return RetrofitInstance.api.getForecastData(
            lat,
            lon,
            unit,
            Constants.API_ID,
            Constants.EXCLUDE
        )
    }
}