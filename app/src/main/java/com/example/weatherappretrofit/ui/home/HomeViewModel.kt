package com.example.weatherappretrofit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherappretrofit.currentweather.model.WeatherModel
import com.example.weatherappretrofit.repository.Repository
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _dateTime = MutableLiveData<String>().apply {
        val sdf = SimpleDateFormat("d MMMM, HH:mm a", Locale.getDefault())
        val date = sdf.format(Date()).toString()
        value = date
    }
    val text: LiveData<String> = _dateTime


    val weatherData = MutableLiveData<WeatherModel>()

    suspend fun getWeatherData(city: String){
        val data = repository.getWeatherData(city)
        weatherData.value = data
    }
}