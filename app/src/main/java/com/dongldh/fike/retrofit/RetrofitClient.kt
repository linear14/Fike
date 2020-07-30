package com.dongldh.fike.retrofit

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.dongldh.fike.data.AppDatabase
import com.dongldh.fike.data.Station
import com.dongldh.fike.data.StationPOJO
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient(val context: Context) {
    val gson = GsonBuilder()
        .setLenient()
        .create()

    // http://openapi.seoul.go.kr:8088/54625942766c696e36386c4258656e/json/bikeList/1/20
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://openapi.seoul.go.kr:8088/54625942766c696e36386c4258656e/json/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClientBuilder().build())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    // For Logging
    private fun okHttpClientBuilder(): OkHttpClient.Builder {
        val okhttpClientBuilder = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        okhttpClientBuilder.addInterceptor(logging)

        return okhttpClientBuilder
    }

    // 이걸 라이브 데이터로?
    val stationsList = mutableSetOf<Station>()

    fun getAllDatasFromRetrofit(start: Int, end: Int, total: Int) {
        val call = this.apiService.getRetrofitData(start.toString(), end.toString())

        call.enqueue(object: Callback<StationPOJO> {
            override fun onFailure(call: Call<StationPOJO>, t: Throwable) {
                Log.d("retrofitError", t.message!!)
                Toast.makeText(context, "서버 접속 실패", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<StationPOJO>, response: Response<StationPOJO>) {
                // Toast.makeText(context, "서버 접속 성공", Toast.LENGTH_SHORT).show()
                Log.d("retrofit", response?.body().toString())
                if(response.isSuccessful) {
                    // Log.d("retrofitData", "데이터 받아오기 성공")

                    val rentBikeStatus = response.body()!!.rentBikeStatus
                    val listTotalCount = rentBikeStatus.listTotalCount
                    val dataArray = rentBikeStatus.row

                    for (data in dataArray) {
                        // 룸 데이터베이스에 값 모두 집어넣기
                        stationsList.add(Station(data.stationId, data.stationName, data.parkingBikeTotCnt, data.latitude, data.longitude))

                        // 동시에 (2km 이내)에 존재하는 데이터일 경우 지도에 표시하는 로직을 추가 (임시로직) 렉 미쳐버려따
                        /*val marker = MapPOIItem()
                        marker.itemName = data.stationName
                        marker.mapPoint = MapPoint.mapPointWithGeoCoord(data.latitude, data.longitude)
                        marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
                        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                        mapView.addPOIItem(marker)*/

                        /*val stationId = data.stationId
                        Log.d("stationId", stationId)*/
                    }
                    if(end <= total + listTotalCount) {
                        getAllDatasFromRetrofit(start+1000, end+1000, total+1000)
                    } else {
                        // 나중에 만들 progress bar 멈추도록 설정 + RoomDatabase에 insert (이런거 나중에 다 MVVM으로 바꾸자)

                        // deprecated 될 예정. 더 좋은 방법은?
                        // 여기가 데이터베이스에 집어넣는 과정! ViewModel과의 연관성 찾아보자 (이 과정을 viewmodel에서 해줘야 할 것 같은데)
                        AsyncTask.execute {
                            val db = AppDatabase.getInstance(context.applicationContext)
                            db.stationDao().insertStations(stationsList)
                        }
                    }

                } else {
                    Log.d("retrofitData", "데이터 받아오기 실패")
                }
            }

        })
    }

}