package com.example.weatherappretrofit.ui.dashboard

data class DailyModel(
    val dayOfWeek: String,
    val dayTemp: String,
    val nightTemp: String,
    val iconCode: String
)