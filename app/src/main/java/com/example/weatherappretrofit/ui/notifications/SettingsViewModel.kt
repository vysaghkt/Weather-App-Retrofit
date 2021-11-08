package com.example.weatherappretrofit.ui.notifications

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _selectedUnit = MutableLiveData<String>()
    val selectedUnit: LiveData<String> = _selectedUnit

    private val storeDataRepository = StoreDataRepository(application)

    fun setSelectedUnit(unit: String) {
        CoroutineScope(Dispatchers.IO).launch {
            storeDataRepository.storeUnits(unit)
        }
    }

    fun getSelectedUnit() {
        CoroutineScope(Dispatchers.Main).launch {
            storeDataRepository.readUnits().collect {
                _selectedUnit.value = it
            }
        }
    }
}