package com.example.weatherappretrofit.ui.dashboard

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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class DailyFragment : Fragment() {

    private val todayViewModel: TodayViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private var _binding: FragmentDailyBinding? = null

    private val forecastList: ArrayList<DailyModel> = arrayListOf()

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

        settingsViewModel.selectedUnit.observe(viewLifecycleOwner,{ unit ->
            if (unit == "Metric" || unit == ""){
                todayViewModel.forecastData.observe(viewLifecycleOwner, {
                    val sdf = SimpleDateFormat("EE, dd MMMM", Locale.ENGLISH)
                    for (i in 0..7) {

                        val dt = it.daily?.get(i)?.dt?.toLong()
                        var date = sdf.format(Date(dt?.times(1000)!!))
                        if (i == 0) {
                            date = getString(R.string.title_home)
                        }
                        val tempDay =
                            (it.daily[i]?.temp?.day?.roundToInt().toString() + getString(R.string.celsius))
                        val tempNight = (it.daily[i]?.temp?.night?.roundToInt()
                            .toString() + getString(R.string.celsius))
                        if (forecastList.size < 8 ) {
                            forecastList.add(
                                DailyModel(
                                    date,
                                    tempDay,
                                    tempNight
                                )
                            )
                        }
                    }
                    binding.recyclerView.adapter = context?.let { it1 ->
                        ForecastAdapter(
                            forecastList,
                            it1
                        )
                    }
                })
            }else{
                todayViewModel.forecastData.observe(viewLifecycleOwner, {
                    val sdf = SimpleDateFormat("EE, dd MMMM", Locale.ENGLISH)
                    for (i in 0..7) {

                        val dt = it.daily?.get(i)?.dt?.toLong()
                        var date = sdf.format(Date(dt?.times(1000)!!))
                        if (i == 0) {
                            date = getString(R.string.title_home)
                        }
                        val tempDay =
                            (it.daily[i]?.temp?.day?.roundToInt().toString() + getString(R.string.fahrenheit))
                        val tempNight = (it.daily[i]?.temp?.night?.roundToInt()
                            .toString() + getString(R.string.fahrenheit))
                        if (forecastList.size < 8) {
                            forecastList.add(
                                DailyModel(
                                    date,
                                    tempDay,
                                    tempNight
                                )
                            )
                        }
                    }
                    binding.recyclerView.adapter = context?.let { it1 ->
                        ForecastAdapter(
                            forecastList,
                            it1
                        )
                    }
                })
            }

        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}