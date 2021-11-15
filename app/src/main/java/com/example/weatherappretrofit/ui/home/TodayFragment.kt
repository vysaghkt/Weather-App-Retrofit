package com.example.weatherappretrofit.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private var isChecked: Boolean = false
    private var isLastEntered: Boolean = false
    private var selectedPosition: Int? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        setFavouriteCityAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        settingsViewModel.getSelectedUnit()
        settingsViewModel.selectedUnit.observe(viewLifecycleOwner, { unitSelected ->

            todayViewModel.instanceSaved.observe(viewLifecycleOwner, {
                if (it != "") {
                    if (isOnline()) {
                        setWeatherDataUI(it, unitSelected)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.no_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    binding.citySearch.setText(it)
                } else {
                    if (isOnline()) {
                        getPresentLocation(unitSelected)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.no_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })

            binding.swipeRefresh.setOnRefreshListener {
                if (isOnline()) {
                    getPresentLocation(unitSelected)
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.citySearch.setText("")
                binding.swipeRefresh.isRefreshing = false
            }

            binding.searchButton.setOnClickListener {
                if (binding.citySearch.text.isEmpty()) {
                    Toast.makeText(activity, getString(R.string.no_city_entered), Toast.LENGTH_LONG)
                        .show()
                } else {
                    if (isOnline()) {
                        setWeatherDataUI(binding.citySearch.text.toString(), unitSelected)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.no_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(it.windowToken, 0)
            }

            binding.mapIcon.setOnClickListener {
                binding.citySearch.setText("")
                if (isOnline()) {
                    getPresentLocation(unitSelected)
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        binding.citySearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                binding.favouriteIcon.setImageResource(R.drawable.ic_baseline_star_outline_24)
                isChecked = false
            }

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

    private fun setFavouriteCityAdapter() {
        todayViewModel.readAllCity?.observe(viewLifecycleOwner, {
            val cityList = mutableListOf<String>()
            val cityIdList = mutableListOf<Int>()
            for (element in it) {
                cityList.add(element.cityName)
                cityIdList.add(element.id)
            }
            val arrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                cityList
            )
            binding.citySearch.setAdapter(arrayAdapter)

            binding.favouriteIcon.setOnClickListener {
                if (binding.citySearch.text.isNotEmpty()) {
                    if (!isChecked) {
                        todayViewModel.addCity(City(0, binding.citySearch.text.toString()))
                        binding.favouriteIcon.setImageResource(R.drawable.favourite_star)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.added_to_favourites),
                            Toast.LENGTH_SHORT
                        ).show()
                        isLastEntered = true
                        isChecked = true
                    } else {
                        if (!isLastEntered) {
                            todayViewModel.deleteCity(
                                City(
                                    cityIdList[selectedPosition!!],
                                    binding.citySearch.text.toString()
                                )
                            )
                        } else {
                            todayViewModel.deleteCity(
                                City(
                                    cityIdList.last(),
                                    binding.citySearch.text.toString()
                                )
                            )
                        }
                        binding.favouriteIcon.setImageResource(R.drawable.ic_baseline_star_outline_24)
                        binding.citySearch.setText("")
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.removed_from_favourites),
                            Toast.LENGTH_SHORT
                        ).show()
                        isChecked = false
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.no_city_entered),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        binding.citySearch.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedPosition = position
                binding.favouriteIcon.setImageResource(R.drawable.favourite_star)
                isChecked = true
                isLastEntered = false
            }
    }

    @SuppressLint("MissingPermission")
    private fun getPresentLocation(unit: String) {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                getForecastWeather(it.latitude, it.longitude, unit)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.set_location),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setWeatherDataUI(city: String, unit: String) {

        todayViewModel.getWeatherData(city)

        todayViewModel.weatherData.observe(viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                val lat = response.body()?.coord?.lat
                val lon = response.body()?.coord?.lon
                getForecastWeather(lat, lon, unit)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.city_not_found),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun getForecastWeather(latitude: Double?, longitude: Double?, unit: String) {

        val degreeUnit = if (unit == "Metric") {
            getString(
                R.string.celsius
            )
        } else {
            getString(
                R.string.fahrenheit
            )
        }
        todayViewModel.getForecastData(latitude!!, longitude!!, unit)
        todayViewModel.forecastData.observe(viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                val sdf =
                    SimpleDateFormat(getString(R.string.today_fragment_time_format), Locale.ENGLISH)
                val updatedDateTime =
                    sdf.format(Date(response.body()?.current?.dt?.toLong()?.times(1000)!!))
                binding.dateTime.text = updatedDateTime
                binding.minMax.text =
                    (getString(R.string.day) + " " + response.body()?.daily?.get(0)?.temp?.day?.roundToInt() + degreeUnit
                            + "  " + getString(R.string.night) + " " + response.body()?.daily?.get(0)?.temp?.night?.roundToInt() + degreeUnit)
                binding.temperature.text =
                    (response.body()?.current!!.temp?.roundToInt().toString() + degreeUnit)
                binding.feelsLike.text =
                    (getString(R.string.feels_like) + " " + response.body()?.current!!.feelsLike?.roundToInt() + degreeUnit)
                binding.climateType.text = response.body()?.current!!.weather?.get(0)?.main
                val icon = response.body()?.current!!.weather?.get(0)?.icon
                Picasso.get()
                    .load(getString(R.string.link_for_icon, icon))
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .into(binding.climateImage)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.city_not_found),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        lastUpdatedTimer()
    }

    private fun lastUpdatedTimer() {
        todayViewModel.resetTimer()
        todayViewModel.startTimer()
        todayViewModel.lastUpdated.observe(viewLifecycleOwner, {
            if (it.toInt() < 60) {
                binding.lastUpdatedTv.text = getString(R.string.last_updated_seconds, it)
            } else {
                val timeInMinute = it.toInt() / 60
                binding.lastUpdatedTv.text =
                    getString(R.string.last_updated_minutes, timeInMinute.toString())
            }
        })

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