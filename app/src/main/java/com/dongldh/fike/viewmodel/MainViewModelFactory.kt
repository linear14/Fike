package com.dongldh.fike.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dongldh.fike.data.Station
import com.dongldh.fike.data.StationRepository

class MainViewModelFactory(
    val stationRepository: StationRepository,
    val stations: Set<Station>
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(stationRepository) as T
    }

}