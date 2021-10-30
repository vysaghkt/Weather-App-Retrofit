package com.example.weatherappretrofit.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.weatherappretrofit.R
import com.example.weatherappretrofit.databinding.FragmentHomeBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import java.lang.Exception
import kotlin.math.roundToInt

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    private lateinit var dateTextView: TextView
    private lateinit var searchButton: Button
    private lateinit var citySearch: EditText
    private lateinit var minMaxTv: TextView
    private lateinit var temperatureTv: TextView
    private lateinit var feelsLikeTv: TextView
    private lateinit var climateType: TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dateTextView = binding.dateTime
        searchButton = binding.searchButton
        citySearch = binding.citySearch
        minMaxTv = binding.minMax
        temperatureTv = binding.temperature
        feelsLikeTv = binding.feelsLike
        climateType = binding.climateType

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            dateTextView.text = it
        })

        hasLocationPermission()

        searchButton.setOnClickListener {
            if (citySearch.text.isEmpty()) {
                Toast.makeText(activity, "Please Enter a City Name", Toast.LENGTH_LONG).show()
            } else {
                setWeatherDataUI()
            }
        }

        return root
    }

    private fun setWeatherDataUI() {

        homeViewModel.getWeatherData(citySearch.text.toString())

        homeViewModel.weatherData.observe(viewLifecycleOwner, Observer {
            val lat = it.coord?.lat
            val lon = it.coord?.lon
            getForecastWeather(lat, lon)
        })
    }

    private fun getForecastWeather(latitude: Double?, longitude: Double?) {

        if (latitude != null && longitude != null) {
            homeViewModel.getForecastData(latitude, longitude)
        }

        homeViewModel.forecastData.observe(viewLifecycleOwner, Observer {
            minMaxTv.text =
                (getString(R.string.day) + " " + it.daily?.get(0)?.temp?.day?.roundToInt() + getString(
                    R.string.celsius
                )
                        + "  " + getString(R.string.night) + " " + it.daily?.get(0)?.temp?.night?.roundToInt() + getString(
                    R.string.celsius
                ))
            temperatureTv.text =
                (it.current?.temp?.roundToInt().toString() + getString(R.string.celsius))
            feelsLikeTv.text =
                (getString(R.string.feels_like) + " " + it.current?.feelsLike?.roundToInt() + getString(
                    R.string.celsius
                ))
            climateType.text = it.current?.weather?.get(0)?.main
            val icon = it.current?.weather?.get(0)?.icon
            Picasso.get()
                .load("http://openweathermap.org/img/wn/$icon@4x.png")
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(binding.climateImage)
        })
    }

    private fun hasLocationPermission() {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        getForecastWeather(0.0, 0.0)
                    } else if (report.isAnyPermissionPermanentlyDenied) {
                        showRationalDialogForPermission()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermission()
                }

            }).onSameThread().check()
    }

    private fun showRationalDialogForPermission() {
        AlertDialog.Builder(activity)
            .setMessage("Location Permission is denied. Please allow it to continue")
            .setPositiveButton("Go To Settings") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context?.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("ACTIVITY", e.toString())
                }
            }
            .setNegativeButton("Exit") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}