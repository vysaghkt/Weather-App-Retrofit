package com.example.weatherappretrofit.forecastweather.retrofit

import com.example.weatherappretrofit.constants.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ForecastRetrofitInstance {

    private val forecastRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val forecastApi: ForecastWeatherApi by lazy {
        forecastRetrofit.create(ForecastWeatherApi::class.java)
    }
}