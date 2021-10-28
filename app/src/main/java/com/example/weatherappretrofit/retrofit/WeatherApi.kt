package com.example.weatherappretrofit.retrofit

import com.example.weatherappretrofit.model.WeatherModel
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/data/2.5/weather")
    suspend fun getWeatherData(
        @Query("q") q: String,
        @Query("units") units: String,
        @Query("appid") appid: String
    ):WeatherModel
}