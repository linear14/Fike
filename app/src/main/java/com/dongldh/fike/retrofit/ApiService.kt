package com.dongldh.fike.retrofit


import com.dongldh.fike.data.StationPOJO
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Allow: GET,OPTIONS,HEAD
interface ApiService {
    @GET("bikeList/{start}/{end}")
    fun getRetrofitData(@Path("start") start: String, @Path("end") end: String): Call<StationPOJO>
}
