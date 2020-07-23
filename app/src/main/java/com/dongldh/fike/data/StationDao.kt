package com.dongldh.fike.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface StationDao {
    @Query("SELECT * FROM stations")
    fun getStations(): List<Station>
}