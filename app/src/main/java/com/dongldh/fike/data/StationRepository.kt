package com.dongldh.fike.data

class StationRepository private constructor(private val stationDao: StationDao) {

    fun getStations() = stationDao.getStations()

    fun insertStations(stations: Set<Station>) = stationDao.insertStations(stations)

    companion object {
        private var instance: StationRepository? = null

        fun getInstance(stationDao: StationDao) =
            instance ?: synchronized(this) {
                instance ?: StationRepository(stationDao).also { instance = it }
            }
    }
}