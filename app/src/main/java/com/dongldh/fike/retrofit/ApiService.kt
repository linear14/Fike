package com.dongldh.fike.retrofit


import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("/bikeList/{start}/{end}")
    fun getRetrofitData(@Path("start") start: String, @Path("end") end: String): Call<JSONObject>
}
