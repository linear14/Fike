package com.dongldh.fike.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dongldh.fike.data.Station
import com.dongldh.fike.data.StationRepository
import com.dongldh.fike.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class MainViewModel(
    private val stationRepository: StationRepository
) : ViewModel() {
    // 레트로핏 처리를 어떻게 해야할지 모르겠네 ㅠㅠ
    // val stations = RetrofitClient(context).stationsList
    // 무슨 기능을 넣을까?
    // 지도와 관련된 기능은 여기에 들어가야 하지 않을까?

    // 모든 station 조회
    fun getStations() = viewModelScope.launch { stationRepository.getStations() }

    // station 모두 insert
    // fun insertStations() = viewModelScope.launch { stationRepository.insertStations(stations) }
}