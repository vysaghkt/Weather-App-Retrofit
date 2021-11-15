package com.example.weatherappretrofit.ui.notifications

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class StoreDataRepository(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("my_preference")

    companion object {
        val UNITS = stringPreferencesKey("units")
    }

    suspend fun storeUnits(unitType: String) {
        context.dataStore.edit {
            it[UNITS] = unitType
        }
    }

    fun readUnits() = context.dataStore.data.map {
        it[UNITS] ?: "Metric"
    }
}