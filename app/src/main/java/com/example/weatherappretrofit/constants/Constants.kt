package com.example.weatherappretrofit.constants

class Constants {

    companion object {
        const val BASE_URL = "https://api.openweathermap.org"
        const val UNITS = "metric"
        const val API_ID = "d5cc3c4b4082f7426f33fd9f7631dfcd"

        val EXCLUDE = listOf("minutely", "hourly", "alert")
    }
}