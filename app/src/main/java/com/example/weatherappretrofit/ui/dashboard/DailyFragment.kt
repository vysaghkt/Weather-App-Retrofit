package com.example.weatherappretrofit.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    companion object {
        private const val FORECAST_MAX_DAYS = 7
    }

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
                getString(R.string.imperial)
            } else {
                getString(R.string.metric)
            }
            todayViewModel.instanceSaved.observe(viewLifecycleOwner, { cityName ->
                if (isOnline()) {
                    if (cityName != "") {
                        getWeatherData(cityName, unit)
                    } else {
                        getPresentLocation(unit)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        })
        return root
    }

    private fun isOnline(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            return true
        }
        return false
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
            val sdf =
                SimpleDateFormat(getString(R.string.daily_fragment_time_format), Locale.ENGLISH)
            for (i in 0..FORECAST_MAX_DAYS) {

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
                val iconLink = getString(R.string.link_for_icon, iconCode)

                forecastList.add(
                    DailyModel(
                        date,
                        tempDay,
                        tempNight,
                        iconLink
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