package com.dongldh.fike.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stations")
data class Station(
    @PrimaryKey
    val stationId: String,
    val stationName: String,
    val parkingBikeTotCnt: Int,
    val latitude: Double,
    val longitude: Double
){
    override fun toString() = stationId
}