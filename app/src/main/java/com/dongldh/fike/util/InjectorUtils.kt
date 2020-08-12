package com.dongldh.fike.util

import android.content.Context
import com.dongldh.fike.data.AppDatabase
import com.dongldh.fike.data.Station
import com.dongldh.fike.data.StationRepository
import com.dongldh.fike.viewmodel.MainViewModelFactory

object InjectorUtils {
    private fun getStationRepository(context: Context): StationRepository {
        return StationRepository.getInstance(AppDatabase.getInstance(context).stationDao())
    }

    fun provideMainViewModelFactory(context: Context): MainViewModelFactory {
        return MainViewModelFactory(getStationRepository(context))
    }
}