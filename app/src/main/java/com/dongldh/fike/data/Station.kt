package com.dongldh.fike.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StationPOJO(
    @SerializedName("rentBikeStatus")
    @Expose
    val rentBikeStatus: RentBikeStatus
){}

data class RentBikeStatus(
    @SerializedName("list_total_count")
    @Expose
    val listTotalCount: Int,
    @SerializedName("row")
    @Expose
    val row: List<Station>

) {}

@Entity(tableName = "stations")
data class Station(
    @SerializedName("stationId")
    @Expose
    @PrimaryKey
    val stationId: String,
    @SerializedName("stationName")
    @Expose
    val stationName: String,
    @SerializedName("parkingBikeTotCnt")
    @Expose
    val parkingBikeTotCnt: Int,
    @SerializedName("stationLatitude")
    @Expose
    val latitude: Double,
    @SerializedName("stationLongitude")
    @Expose
    val longitude: Double

) {}
