package com.example.weatherappretrofit.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.example.weatherappretrofit.currentweather.model.WeatherModel
import com.example.weatherappretrofit.forecastweather.model.ForecastWeatherModel
import com.example.weatherappretrofit.repository.Repository
import com.example.weatherappretrofit.roomdatabase.City
import com.example.weatherappretrofit.roomdatabase.CityDatabase
import com.example.weatherappretrofit.roomdatabase.CityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

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

    fun getForecastData(lat: Double, lon: Double, unit: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val data = repository.getForecastData(lat, lon, unit)
            forecastData.value = data
        }
    }

    //Code for Room Database

    var readAllCity: LiveData<List<City>>? = null
    private var cityRepository: CityRepository? = null

    init {
        val cityDao = CityDatabase.getDatabaseInstance(application).cityDao()
        cityRepository = CityRepository(cityDao)
        readAllCity = cityRepository!!.getAllData
    }

    fun addCity(city: City) {
        viewModelScope.launch {
            cityRepository?.addCity(city)
        }
    }
}