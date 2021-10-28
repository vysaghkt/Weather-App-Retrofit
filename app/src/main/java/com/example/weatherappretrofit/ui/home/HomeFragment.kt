package com.example.weatherappretrofit.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherappretrofit.databinding.FragmentHomeBinding
import com.example.weatherappretrofit.retrofit.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    private lateinit var dateTextView: TextView
    private lateinit var searchButton: Button
    private lateinit var citySearch: EditText

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dateTextView = binding.dateTime
        searchButton = binding.searchButton
        citySearch = binding.citySearch

        val repository = Repository()
        val viewModelFactory = HomeViewModelFactory(repository)
        homeViewModel =
            ViewModelProvider(this,viewModelFactory).get(HomeViewModel::class.java)

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            dateTextView.text = it
        })

        searchButton.setOnClickListener {
            if (citySearch.text.isEmpty()){
                Toast.makeText(activity, "Please Enter a City Name", Toast.LENGTH_LONG).show()
            }else{
                setWeatherDataUI()
            }
        }

        return root
    }

    private fun setWeatherDataUI(){
        CoroutineScope(Dispatchers.Main).launch {
            homeViewModel.getWeatherData(citySearch.text.toString())
        }

        homeViewModel.weatherData.observe(viewLifecycleOwner, Observer {
            Log.e("KOTLIN",it.name.toString())
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}