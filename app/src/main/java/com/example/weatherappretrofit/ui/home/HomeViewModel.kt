package com.example.weatherappretrofit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherappretrofit.currentweather.model.WeatherModel
import com.example.weatherappretrofit.forecastweather.model.ForecastWeatherModel
import com.example.weatherappretrofit.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel() : ViewModel() {

    private val repository = Repository()

    private val _dateTime = MutableLiveData<String>().apply {
        val sdf = SimpleDateFormat("d MMMM, hh:mm a", Locale.getDefault())
        val date = sdf.format(Date()).toString()
        value = date
    }
    val text: LiveData<String> = _dateTime

    val weatherData = MutableLiveData<WeatherModel>()

    fun getWeatherData(city: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val data = repository.getWeatherData(city)
            weatherData.value = data
        }
    }

    val forecastData = MutableLiveData<ForecastWeatherModel>()

    fun getForecastData(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.Main) {
            val data = repository.getForecastData(lat, lon)
            forecastData.value = data
        }
    }
}