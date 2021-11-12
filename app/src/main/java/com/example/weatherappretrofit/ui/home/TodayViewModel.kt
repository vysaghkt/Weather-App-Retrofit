package com.example.weatherappretrofit.ui.home

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.*
import com.example.weatherappretrofit.model.WeatherModel
import com.example.weatherappretrofit.forecastweather.model.ForecastWeatherModel
import com.example.weatherappretrofit.repository.Repository
import com.example.weatherappretrofit.roomdatabase.City
import com.example.weatherappretrofit.roomdatabase.CityDatabase
import com.example.weatherappretrofit.roomdatabase.CityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class TodayViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository()

    private val _weatherData = MutableLiveData<Response<WeatherModel>>()
    val weatherData: LiveData<Response<WeatherModel>> = _weatherData

    fun getWeatherData(city: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val data = repository.getWeatherData(city)
            _weatherData.value = data
        }
    }

    private val _forecastData = MutableLiveData<Response<ForecastWeatherModel>>()
    val forecastData: LiveData<Response<ForecastWeatherModel>> = _forecastData

    fun getForecastData(lat: Double, lon: Double, unit: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val data = repository.getForecastData(lat, lon, unit)
            _forecastData.value = data
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

    fun deleteCity(city: City) {
        viewModelScope.launch {
            cityRepository?.deleteCity(city)
        }
    }

    //Saving Instance
    private val _instanceSaved = MutableLiveData("")
    val instanceSaved: LiveData<String> = _instanceSaved

    fun setInstance(cityName: String) {
        _instanceSaved.value = cityName
    }

    //Last Updated Timer
    private val _lastUpdated = MutableLiveData<String>()
    val lastUpdated: LiveData<String> = _lastUpdated

    private val timer = object : CountDownTimer(600000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _lastUpdated.value = ((600000 - millisUntilFinished) / 1000).toString()
        }

        override fun onFinish() {

        }
    }

    fun startTimer() {
        timer.start()
    }

    fun resetTimer() {
        timer.cancel()
    }
}