package com.dongldh.fike

import android.content.Context
import android.graphics.Camera
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.dongldh.fike.data.*
import com.dongldh.fike.retrofit.RetrofitClient
import com.dongldh.fike.util.DEFAULT_ZOOM_LEVEL
import com.dongldh.fike.util.Hash
import com.dongldh.fike.util.Permissions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
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
class MainActivity : AppCompatActivity(), MapView.MapViewEventListener, MapView.CurrentLocationEventListener {
    var myLocationMapPoint: MapPoint? = null
    lateinit var map: MapView
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

        RetrofitClient(applicationContext).getAllDatasFromRetrofit(1, 1000, 0)

        findBikeFab.setOnClickListener {
            map.removeAllPOIItems()
            AsyncTask.execute {
                val list = AppDatabase.getInstance(applicationContext).stationDao().getStations()
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
                    }
                }
            }
        }


        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
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
    }

    fun distanceByDegree(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val start = Location("A")
        val end = Location("B")

        start.latitude = lat1
        start.longitude = lon1
        end.latitude = lat2
        end.longitude = lon2

        return start.distanceTo(end).toDouble()
    }

    fun calDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = sin(degToRad(lat1)) * sin(degToRad(lat2)) +
                cos(degToRad(lat1)) * cos(degToRad(lat2)) * cos(degToRad(theta))
        dist = acos(dist)
        dist = radToDeg(dist)

        return dist * 60 * 1.1515 * 1.609344 * 1000
    }

    fun degToRad(deg: Double): Double = (deg * Math.PI) / 180
    fun radToDeg(rad: Double): Double = (rad * 180 / Math.PI)


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

}