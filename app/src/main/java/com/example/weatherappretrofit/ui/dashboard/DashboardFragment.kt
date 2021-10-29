package com.example.weatherappretrofit.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherappretrofit.R
import com.example.weatherappretrofit.databinding.FragmentDashboardBinding
import com.example.weatherappretrofit.ui.home.HomeViewModel

class DashboardFragment : Fragment() {

    private val dashboardViewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dashboardViewModel.forecastData.observe(viewLifecycleOwner, Observer {
            Log.d("FORECAST", it.timezoneOffset.toString())
        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}