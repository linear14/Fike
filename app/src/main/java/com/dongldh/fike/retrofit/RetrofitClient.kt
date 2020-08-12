package com.dongldh.fike.retrofit

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dongldh.fike.data.Station
import com.dongldh.fike.data.StationPOJO
import com.dongldh.fike.util.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RetrofitClient {
    val tempStationList = mutableListOf<Station>()
    fun getAllDatasFromRetrofit(
        start: Int,
        end: Int,
        total: Int,
        stationList: MutableLiveData<List<Station>>,
        latitude: Double,
        longitude: Double
    ) {
        if(start == 1) {
            tempStationList.clear()
        }

        val call = FikeRetrofit.getApiService.getRetrofitData(start.toString(), end.toString())

        call.enqueue(object: Callback<StationPOJO> {
            override fun onFailure(call: Call<StationPOJO>, t: Throwable) {
                Log.d("retrofitError", t.message!!)
                //Toast.makeText(context, "서버 접속 실패", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<StationPOJO>, response: Response<StationPOJO>) {
                // Toast.makeText(context, "서버 접속 성공", Toast.LENGTH_SHORT).show()
                Log.d("retrofit", response.body().toString())
                if(response.isSuccessful) {
                    // Log.d("retrofitData", "데이터 받아오기 성공")

                    val rentBikeStatus = response.body()!!.rentBikeStatus
                    val listTotalCount = rentBikeStatus.listTotalCount
                    val dataArray = rentBikeStatus.row

                    for (station in dataArray) {
                            val distance = Util.distanceByDegree(station.latitude, station.longitude, latitude, longitude)

                            if(distance <= 1000) {
                                tempStationList.add(Station(station.stationId, station.stationName, station.parkingBikeTotCnt, station.latitude, station.longitude, distance.toInt()))
                            }
                    }
                    if(end <= total + listTotalCount) {
                        getAllDatasFromRetrofit(start+1000, end+1000, total+1000, stationList, latitude, longitude)
                    } else {
                        // 나중에 만들 progress bar 멈추도록 설정 + RoomDatabase에 insert (이런거 나중에 다 MVVM으로 바꾸자)

                        tempStationList.sortBy { it.distance }
                        stationList.value = tempStationList
                    }

                } else {
                    Log.d("retrofitData", "데이터 받아오기 실패")
                }
            }

        })
    }

}