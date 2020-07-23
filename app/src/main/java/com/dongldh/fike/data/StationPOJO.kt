package com.dongldh.fike.data

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
    @SerializedName("RESULT")
    @Expose
    val result: Result,
    @SerializedName("row")
    @Expose
    val row: List<Row>

) {}

data class Result(
    @SerializedName("CODE")
    @Expose
    val code: String,
    @SerializedName("MESSAGE")
    @Expose
    val message: String
){}

data class Row(
    @SerializedName("stationId")
    @Expose
    val stationId: String,
    @SerializedName("stationName")
    @Expose
    val stationName: String,
    @SerializedName("parkingBikeToCnt")
    @Expose
    val parkingBikeToCnt: Int,
    @SerializedName("stationLatitude")
    @Expose
    val latitude: Double,
    @SerializedName("stationLongitude")
    @Expose
    val longitude: Double,
    @SerializedName("rackToCnt")
    @Expose
    val rackToCnt: Int,
    @SerializedName("shared")
    @Expose
    val shared: Int
) {}
