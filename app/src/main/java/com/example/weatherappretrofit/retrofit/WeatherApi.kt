package com.example.weatherappretrofit.retrofit

import com.example.weatherappretrofit.model.WeatherModel
import com.example.weatherappretrofit.forecastweather.model.ForecastWeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/data/2.5/weather")
    suspend fun getWeatherData(
        @Query("q") q: String,
        @Query("units") units: String,
        @Query("appid") appid: String
    ): Response<WeatherModel>

    @GET("/data/2.5/onecall")
    suspend fun getForecastData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") appid: String,
        @Query("exclude") exclude: List<String>
    ): Response<ForecastWeatherModel>
}