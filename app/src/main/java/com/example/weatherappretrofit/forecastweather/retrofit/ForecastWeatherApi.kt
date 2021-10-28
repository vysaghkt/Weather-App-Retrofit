package com.example.weatherappretrofit.forecastweather.retrofit

import com.example.weatherappretrofit.forecastweather.ForecastWeatherModel
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastWeatherApi {

    @GET("/data/2.5/onecall")
    suspend fun getForecastData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") appid: String,
        @Query("exclude") exclude: List<String>
    ): ForecastWeatherModel
}