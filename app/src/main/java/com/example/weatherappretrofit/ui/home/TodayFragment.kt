package com.example.weatherappretrofit.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.weatherappretrofit.R
import com.example.weatherappretrofit.databinding.FragmentTodayBinding
import com.example.weatherappretrofit.roomdatabase.City
import com.example.weatherappretrofit.ui.notifications.SettingsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class TodayFragment : Fragment() {

    private val todayViewModel: TodayViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()

    private var _binding: FragmentTodayBinding? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        todayViewModel.readAllCity?.observe(viewLifecycleOwner, {
            val cityList = mutableListOf<String>()
            for (element in it) {
                cityList.add(element.cityName)
            }
            val arrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                cityList
            )
            binding.citySearch.setAdapter(arrayAdapter)
        })

        settingsViewModel.getSelectedUnit()
        settingsViewModel.selectedUnit.observe(viewLifecycleOwner, { unitSelected ->

            todayViewModel.instanceSaved.observe(viewLifecycleOwner, {
                if (it != "") {
                    setWeatherDataUI(it, unitSelected)
                    binding.citySearch.setText(it)
                } else {
                    getPresentLocation(unitSelected)
                }
            })

            binding.swipeRefresh.setOnRefreshListener {
                getPresentLocation(unitSelected)
                binding.citySearch.setText("")
                binding.swipeRefresh.isRefreshing = false
            }

            binding.searchButton.setOnClickListener {
                if (binding.citySearch.text.isEmpty()) {
                    Toast.makeText(activity, "Please Enter a City Name", Toast.LENGTH_LONG).show()
                } else {
                    setWeatherDataUI(binding.citySearch.text.toString(), unitSelected)
                }
                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(it.windowToken, 0)
            }

            binding.mapIcon.setOnClickListener {
                binding.citySearch.setText("")
                getPresentLocation(unitSelected)
            }

            binding.favouriteIcon.setOnClickListener {
                todayViewModel.addCity(City(0, binding.citySearch.text.toString()))
                setSelectedBackground()
            }
        })

        return root
    }

    @SuppressLint("MissingPermission")
    private fun getPresentLocation(unit: String) {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                getForecastWeather(it.latitude, it.longitude, unit)
            } else {
                getForecastWeather(0.0, 0.0, unit)
            }
        }
    }

    private fun setSelectedBackground() {
        object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.favouriteIcon.setImageResource(R.drawable.favourite_star)
            }

            override fun onFinish() {
                binding.favouriteIcon.setImageResource(R.drawable.ic_baseline_star_outline_24)
            }

        }.start()
    }

    private fun setWeatherDataUI(city: String, unit: String) {

        todayViewModel.getWeatherData(city)

        todayViewModel.weatherData.observe(viewLifecycleOwner, {
            val lat = it.coord?.lat
            val lon = it.coord?.lon
            getForecastWeather(lat, lon, unit)
        })
    }

    private fun getForecastWeather(latitude: Double?, longitude: Double?, unit: String) {

        if (unit == "Metric" || unit == "") {
            todayViewModel.getForecastData(latitude!!, longitude!!, "Metric")
            todayViewModel.forecastData.observe(viewLifecycleOwner, {
                val sdf = SimpleDateFormat("dd MMMM, hh:mm", Locale.getDefault())
                val updatedDateTime = sdf.format(Date(it?.current?.dt?.toLong()?.times(1000)!!))
                binding.dateTime.text = updatedDateTime
                binding.minMax.text =
                    (getString(R.string.day) + " " + it.daily?.get(0)?.temp?.day?.roundToInt() + getString(
                        R.string.celsius
                    )
                            + "  " + getString(R.string.night) + " " + it.daily?.get(0)?.temp?.night?.roundToInt() + getString(
                        R.string.celsius
                    ))
                binding.temperature.text =
                    (it.current.temp?.roundToInt().toString() + getString(R.string.celsius))
                binding.feelsLike.text =
                    (getString(R.string.feels_like) + " " + it.current.feelsLike?.roundToInt() + getString(
                        R.string.celsius
                    ))
                binding.climateType.text = it.current.weather?.get(0)?.main
                val icon = it.current.weather?.get(0)?.icon
                Picasso.get()
                    .load("http://openweathermap.org/img/wn/$icon@4x.png")
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .into(binding.climateImage)
            })
        } else {
            todayViewModel.getForecastData(latitude!!, longitude!!, unit)
            todayViewModel.forecastData.observe(viewLifecycleOwner, {
                binding.minMax.text =
                    (getString(R.string.day) + " " + it.daily?.get(0)?.temp?.day?.roundToInt() + getString(
                        R.string.fahrenheit
                    )
                            + "  " + getString(R.string.night) + " " + it.daily?.get(0)?.temp?.night?.roundToInt() + getString(
                        R.string.fahrenheit
                    ))
                binding.temperature.text =
                    (it.current?.temp?.roundToInt().toString() + getString(R.string.fahrenheit))
                binding.feelsLike.text =
                    (getString(R.string.feels_like) + " " + it.current?.feelsLike?.roundToInt() + getString(
                        R.string.fahrenheit
                    ))
                binding.climateType.text = it.current?.weather?.get(0)?.main
                val icon = it.current?.weather?.get(0)?.icon
                Picasso.get()
                    .load("http://openweathermap.org/img/wn/$icon@4x.png")
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .into(binding.climateImage)
            })
        }
    }

    override fun onPause() {
        super.onPause()

        if (binding.citySearch.text.toString() != "") {
            todayViewModel.setInstance(binding.citySearch.text.toString())
        } else {
            todayViewModel.setInstance("")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}