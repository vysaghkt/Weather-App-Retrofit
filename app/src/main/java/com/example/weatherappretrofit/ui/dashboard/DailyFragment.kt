package com.example.weatherappretrofit.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.weatherappretrofit.R
import com.example.weatherappretrofit.databinding.FragmentDailyBinding
import com.example.weatherappretrofit.ui.home.TodayViewModel
import com.example.weatherappretrofit.ui.notifications.SettingsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class DailyFragment : Fragment() {

    private val todayViewModel: TodayViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private var _binding: FragmentDailyBinding? = null

    private val forecastList = mutableListOf<DailyModel>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDailyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        settingsViewModel.getSelectedUnit()
        settingsViewModel.selectedUnit.observe(viewLifecycleOwner, {
            val unit = if (it == "Imperial") {
                "Imperial"
            } else {
                "Metric"
            }
            todayViewModel.instanceSaved.observe(viewLifecycleOwner, { cityName ->
                if (cityName != "") {
                    getWeatherData(cityName, unit)
                } else {
                    getPresentLocation(unit)
                }
            })
        })
        return root
    }

    @SuppressLint("MissingPermission")
    private fun getPresentLocation(unit: String) {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                getForecastWeatherData(it.latitude, it.longitude, unit)
            } else {
                getForecastWeatherData(0.0, 0.0, unit)
            }
        }
    }

    private fun getWeatherData(city: String, unit: String) {
        todayViewModel.getWeatherData(city)
        todayViewModel.weatherData.observe(viewLifecycleOwner, {
            getForecastWeatherData(it.coord?.lat!!, it.coord.lon!!, unit)
        })
    }

    private fun getForecastWeatherData(lat: Double, lon: Double, unit: String) {
        todayViewModel.getForecastData(lat, lon, unit)
        setRecyclerview(unit)
    }

    private fun setRecyclerview(unit: String) {

        val degreeUnit = if (unit == "Imperial") {
            getString(R.string.fahrenheit)
        } else {
            getString(R.string.celsius)
        }

        todayViewModel.forecastData.observe(viewLifecycleOwner, {
            forecastList.clear()
            val sdf = SimpleDateFormat("EE, dd MMMM", Locale.ENGLISH)
            for (i in 0..7) {

                val dt = it.daily?.get(i)?.dt?.toLong()
                var date = sdf.format(Date(dt?.times(1000)!!))
                if (i == 0) {
                    date = getString(R.string.title_home)
                }
                val tempDay =
                    (it.daily[i]?.temp?.day?.roundToInt()
                        .toString() + degreeUnit)
                val tempNight = (it.daily[i]?.temp?.night?.roundToInt()
                    .toString() + degreeUnit)
                val iconCode = it.daily[i]?.weather?.get(0)?.icon!!

                forecastList.add(
                    DailyModel(
                        date,
                        tempDay,
                        tempNight,
                        iconCode
                    )
                )
            }
            binding.recyclerView.adapter = context?.let { it1 ->
                ForecastAdapter(
                    forecastList,
                    it1
                )
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}