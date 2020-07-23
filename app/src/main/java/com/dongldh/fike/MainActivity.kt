package com.dongldh.fike

import android.content.Context
import android.graphics.Camera
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.dongldh.fike.data.*
import com.dongldh.fike.retrofit.RetrofitClient
import com.dongldh.fike.util.DEFAULT_ZOOM_LEVEL
import com.dongldh.fike.util.Hash
import com.dongldh.fike.util.Permissions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import net.daum.mf.map.api.CameraPosition
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 인터페이스를 이런식으로 액티비티 내에서 직접 구현해서 사용해야하네... object로 넣어주면 먹히지가 않음..ㅋㅋ
class MainActivity : AppCompatActivity(), MapView.MapViewEventListener, MapView.CurrentLocationEventListener {
    var myLocationMapPoint: MapPoint? = null
    lateinit var map: MapView
    val stationsList = mutableSetOf<Station>()
    // var stationRepository: StationRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Permissions(this).permissionLocation()
        // stationRepository = StationRepository.getInstance(StationDao)
        // Hash(this).getAppKeyHash()

        map = MapView(this)
        mapView.addView(map)
        map.setZoomLevelFloat(DEFAULT_ZOOM_LEVEL, false)

        myLocationFab.setOnClickListener {
            map.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
            map.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(myLocationMapPoint, DEFAULT_ZOOM_LEVEL)))
        }

        // 데이터가 Room에 잘 들어갔나 테스트할려고 작성한 코드입니다. (나중에 삭제 예정) 됐당!
        searchFab.setOnClickListener {
            AsyncTask.execute {
                val db = AppDatabase.getInstance(applicationContext)
                val list = db.stationDao().getStations()
                for(i in 0..30) {
                    Log.d("dataTest", "${list[i].stationId}, ${list[i].stationName}")
                }
            }
        }

        map.setMapViewEventListener(this)
        map.setCurrentLocationEventListener(this)

        getAllDatasFromRetrofit(1, 1000, 0)
    }

    fun getAllDatasFromRetrofit(start: Int, end: Int, total: Int) {
        val retrofitClient = RetrofitClient()
        val call = retrofitClient.apiService.getRetrofitData(start.toString(), end.toString())
        call.enqueue(object: Callback<StationPOJO> {
            override fun onFailure(call: Call<StationPOJO>, t: Throwable) {
                Log.d("retrofitError", t.message!!)
                Toast.makeText(this@MainActivity, "서버 접속 실패", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<StationPOJO>, response: Response<StationPOJO>) {
                Toast.makeText(this@MainActivity, "서버 접속 성공", Toast.LENGTH_SHORT).show()
                Log.d("retrofit", response?.body().toString())
                if(response.isSuccessful) {
                    Log.d("retrofitData", "데이터 받아오기 성공")

                    val rentBikeStatus = response.body()!!.rentBikeStatus
                    val listTotalCount = rentBikeStatus.listTotalCount
                    val dataArray = rentBikeStatus.row

                    for (data in dataArray) {
                        // 룸 데이터베이스에 값 모두 집어넣기
                        stationsList.add(Station(data.stationId, data.stationName, data.parkingBikeToCnt, data.latitude, data.longitude))
                        /*val stationId = data.stationId
                        Log.d("stationId", stationId)*/
                    }
                    if(end <= total + listTotalCount) {
                        getAllDatasFromRetrofit(start+1000, end+1000, total+1000)
                    } else {
                        // 나중에 만들 progress bar 멈추도록 설정 + RoomDatabase에 insert (이런거 나중에 다 MVVM으로 바꾸자)

                        // deprecated 될 예정. 더 좋은 방법은?
                        AsyncTask.execute {
                            val db = AppDatabase.getInstance(applicationContext)
                            db.stationDao().deleteAll()
                            db.stationDao().insertStations(stationsList)
                        }
                    }

                } else {
                    Log.d("retrofitData", "데이터 받아오기 실패")
                }
            }

        })
    }


    // MapView.MapViewEventListener
    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewInitialized(p0: MapView?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
        map.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
    }


    // MapView.CurrentLocationEventListener
    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
    }

    override fun onCurrentLocationUpdate(p0: MapView?, point: MapPoint?, p2: Float) {
        myLocationMapPoint = point
    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {
    }

}