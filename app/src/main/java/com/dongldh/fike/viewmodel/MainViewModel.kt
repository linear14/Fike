package com.dongldh.fike.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dongldh.fike.data.Station
import com.dongldh.fike.data.StationRepository
import com.dongldh.fike.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class MainViewModel(
    private val stationRepository: StationRepository
) : ViewModel() {
    var stationList: MutableLiveData<List<Station>> = MutableLiveData()

    // 모든 station 조회
    fun getStations() = viewModelScope.launch { stationRepository.getStations() }

    fun getStationListFromRetrofit(latitude: Double, longitude: Double) {
        RetrofitClient.getAllDatasFromRetrofit(1, 1000, 0, stationList, latitude, longitude)
    }

    // station 모두 insert
    // fun insertStations() = viewModelScope.launch { stationRepository.insertStations(stations) }
}