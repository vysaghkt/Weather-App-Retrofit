package com.example.weatherappretrofit.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.weatherappretrofit.R
import com.example.weatherappretrofit.databinding.FragmentDashboardBinding
import com.example.weatherappretrofit.ui.home.HomeViewModel
import com.example.weatherappretrofit.ui.notifications.NotificationsViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class DashboardFragment : Fragment() {

    private val dashboardViewModel: HomeViewModel by activityViewModels()
    private val notificationViewModel: NotificationsViewModel by activityViewModels()
    private var _binding: FragmentDashboardBinding? = null

    private val forecastList: ArrayList<DailyModel> = arrayListOf()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        notificationViewModel.selectedUnit.observe(viewLifecycleOwner,{unit ->
            if (unit == "Metric" || unit == ""){
                dashboardViewModel.forecastData.observe(viewLifecycleOwner, Observer {
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
                dashboardViewModel.forecastData.observe(viewLifecycleOwner, Observer {
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