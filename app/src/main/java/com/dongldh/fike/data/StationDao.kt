package com.dongldh.fike.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import retrofit2.http.DELETE

@Dao
interface StationDao {
    @Query("SELECT * FROM stations")
    fun getStations(): List<Station>

    @Insert
    fun insertStations(stations: Set<Station>)

    @Query("DELETE FROM stations")
    fun deleteAll()
}