package com.dongldh.fike

import android.content.Context
import android.graphics.Camera
import android.graphics.Color
import android.location.Location
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.dongldh.fike.adapter.ResultAdapter
import com.dongldh.fike.data.*
import com.dongldh.fike.retrofit.RetrofitClient
import com.dongldh.fike.util.DEFAULT_ZOOM_LEVEL
import com.dongldh.fike.util.Hash
import com.dongldh.fike.util.Permissions
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_result.*
import kotlinx.android.synthetic.main.fragment_result.view.*
import kotlinx.android.synthetic.main.fragment_result.view.recycler
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import net.daum.mf.map.api.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

// 인터페이스를 이런식으로 액티비티 내에서 직접 구현해서 사용해야하네... object로 넣어주면 먹히지가 않음..ㅋㅋ
class MainActivity : AppCompatActivity(), MapView.MapViewEventListener, MapView.CurrentLocationEventListener, MapView.POIItemEventListener {
    var myLocationMapPoint: MapPoint? = null
    lateinit var map: MapView
    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    var nowResultStyle = 0 // 0 이면 거리순, 1 이면 남은 자전거순
    lateinit var selectedPairList: MutableList<Pair<Station, Double>>

    // var stationRepository: StationRepository? = null

    @RequiresApi(Build.VERSION_CODES.M)
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

        map.setMapViewEventListener(this)
        map.setCurrentLocationEventListener(this)
        map.setPOIItemEventListener(this)

        RetrofitClient(applicationContext).getAllDatasFromRetrofit(1, 1000, 0)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.halfExpandedRatio = 0.4f
        bottomSheetBehavior.isFitToContents = false

        bottomSheetBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        myLocationFab.hide()
                        findBikeFab.hide()
                    }
                    else -> {
                        myLocationFab.show()
                        findBikeFab.show()
                    }
                }
            }

        })

        // 지정된 Station을 거리와 함께 map으로 저장
        selectedPairList = mutableListOf<Pair<Station, Double>>()

        findBikeFab.setOnClickListener {
            // 초기화 작업
            map.removeAllPOIItems()
            selectedPairList.clear()
            
            AsyncTask.execute {
                val list = AppDatabase.getInstance(applicationContext).stationDao().getStations()
                // gps 켜져있으면 내 위치 기준. 안 켜져 있으면 지도 중심 기준
                for(station in list) {
                    val distance = distanceByDegree(
                        station.latitude,
                        station.longitude,
                        myLocationMapPoint?.mapPointGeoCoord?.latitude ?: map.mapCenterPoint.mapPointGeoCoord.latitude,
                        myLocationMapPoint?.mapPointGeoCoord?.longitude ?: map.mapCenterPoint.mapPointGeoCoord.longitude
                    )

                    if(distance <= 1000) {
                        Log.d("distance", "${distance}m")
                        val marker = MapPOIItem()
                        marker.itemName = station.stationName
                        marker.mapPoint = MapPoint.mapPointWithGeoCoord(station.latitude, station.longitude)
                        marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
                        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                        map.addPOIItem(marker)

                        selectedPairList.add(Pair(station, distance))
                    }
                }
                if(nowResultStyle == 0) listOrderingByDistance(selectedPairList) else listOrderingByRemainingBikes(selectedPairList)
            }
        }

        bottomSheet.show_distance.setOnClickListener {
            nowResultStyle = 0
            bottomSheet.show_distance.setTextColor(Color.WHITE)
            bottomSheet.show_distance.setBackgroundResource(R.drawable.selected_box)
            bottomSheet.show_remain_bikes.setTextColor(Color.DKGRAY)
            bottomSheet.show_remain_bikes.setBackgroundResource(R.drawable.not_selected_box)
            listOrderingByDistance(selectedPairList)
        }

        bottomSheet.show_remain_bikes.setOnClickListener {
            nowResultStyle = 1
            bottomSheet.show_distance.setTextColor(Color.DKGRAY)
            bottomSheet.show_distance.setBackgroundResource(R.drawable.not_selected_box)
            bottomSheet.show_remain_bikes.setTextColor(Color.WHITE)
            bottomSheet.show_remain_bikes.setBackgroundResource(R.drawable.selected_box)
            listOrderingByRemainingBikes(selectedPairList)
        }

    }
    private fun changeRecyclerViewStateOnUiThread(list: MutableList<Pair<Station, Double>>) {
        runOnUiThread() {
            bottomSheet.recycler.layoutManager = LinearLayoutManager(this)
            bottomSheet.recycler.adapter = ResultAdapter(context = this, list = list, map = map, bottomSheetBehavior = bottomSheetBehavior, recycler = recycler)
            if(bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
    }

    private fun listOrderingByDistance(list: MutableList<Pair<Station, Double>>) {
        list.sortBy { it.second }
        changeRecyclerViewStateOnUiThread(list)
    }

    private fun listOrderingByRemainingBikes(list: MutableList<Pair<Station, Double>>) {
        list.sortByDescending { it.first.parkingBikeTotCnt }
        changeRecyclerViewStateOnUiThread(list)
    }


    private fun distanceByDegree(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val start = Location("A")
        val end = Location("B")

        start.latitude = lat1
        start.longitude = lon1
        end.latitude = lat2
        end.longitude = lon2

        return start.distanceTo(end).toDouble()
    }

    /*fun calDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = sin(degToRad(lat1)) * sin(degToRad(lat2)) +
                cos(degToRad(lat1)) * cos(degToRad(lat2)) * cos(degToRad(theta))
        dist = acos(dist)
        dist = radToDeg(dist)

        return dist * 60 * 1.1515 * 1.609344 * 1000
    }

    fun degToRad(deg: Double): Double = (deg * Math.PI) / 180
    fun radToDeg(rad: Double): Double = (rad * 180 / Math.PI)*/


    // MapView.MapViewEventListener
    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewInitialized(p0: MapView?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {
        map.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewZoomLevelChanged(map: MapView, zoom: Int) {
        if(zoom >= 7.3f) map.setZoomLevelFloat(7.0f, false)
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


    // MapView.POIItemEventListener
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }

    override fun onPOIItemSelected(mapView: MapView, item: MapPOIItem) {
        mapView.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(item.mapPoint, mapView.zoomLevelFloat)))
        val smoothScroller: RecyclerView.SmoothScroller by lazy {
            object : LinearSmoothScroller(this) {
                override fun getVerticalSnapPreference(): Int = SNAP_TO_START
            }
        }
        // 선택한 아이템의 position을 구하고, recyclerView 맨 위에 보이도록 올려주는 작업
        for(index in 0 until selectedPairList.size) {
            if(selectedPairList[index].first.stationName == item.itemName) {
                smoothScroller.targetPosition = index
                break
            }
        }
        bottomSheet.recycler.layoutManager?.startSmoothScroll(smoothScroller)
    }

}