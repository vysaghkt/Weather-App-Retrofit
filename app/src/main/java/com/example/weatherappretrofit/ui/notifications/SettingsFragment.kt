package com.example.weatherappretrofit.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.weatherappretrofit.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val settingsViewModel: SettingsViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        settingsViewModel.getSelectedUnit()
        settingsViewModel.selectedUnit.observe(viewLifecycleOwner, {
            setSelectedOptionSpinner(it)
        })

        return root
    }

    private fun setSelectedOptionSpinner(unit: String) {

        val unitsList = if (unit == "Metric" || unit == "") {
            listOf("Metric", "Imperial")
        } else {
            listOf("Imperial", "Metric")
        }

        val arrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, unitsList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = arrayAdapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                settingsViewModel.setSelectedUnit(
                    parent?.getItemAtPosition(position).toString()
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}