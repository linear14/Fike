package com.dongldh.fike

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.dongldh.fike.adapter.ResultAdapter
import com.dongldh.fike.data.Station
import com.dongldh.fike.databinding.ActivityMainBinding
import com.dongldh.fike.util.DEFAULT_ZOOM_LEVEL
import com.dongldh.fike.util.InjectorUtils
import com.dongldh.fike.util.Permissions
import com.dongldh.fike.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import net.daum.mf.map.api.*

class MainActivity : AppCompatActivity(), MapView.MapViewEventListener, MapView.CurrentLocationEventListener, MapView.POIItemEventListener {

    private val mainViewModel: MainViewModel by viewModels { InjectorUtils.provideMainViewModelFactory(this) }

    lateinit var map: MapView
    var myLocationMapPoint: MapPoint? = null

    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.apply {
            isOrderingByDistance = true

            callback = object: Callback {
                override fun setShowingMethodViewStyle(selected: Int) {
                    isOrderingByDistance = (selected == 0)
                    if(selected == 0) {
                        mainViewModel.listOrderingByDistance()
                    } else {
                        mainViewModel.listOrderingByRemainingBikes()
                    }
                }
            }
        }

        Permissions(this).permissionLocation()
        mapViewAndBottomSheetInit()
        // Hash(this).getAppKeyHash()

        val adapter = ResultAdapter()
        binding.recycler.adapter = adapter

        myLocationFab.setOnClickListener {
            findMyLocationAndMoveCamera()
        }

        findBikeFab.setOnClickListener {
            if(bottomSheetBehavior.state != BottomSheetBehavior.STATE_HALF_EXPANDED) bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            map.removeAllPOIItems()
            mainViewModel.getStationListFromRetrofit(
                myLocationMapPoint?.mapPointGeoCoord?.latitude ?: map.mapCenterPoint.mapPointGeoCoord.latitude,
                myLocationMapPoint?.mapPointGeoCoord?.longitude ?: map.mapCenterPoint.mapPointGeoCoord.longitude
            )
        }

        mainViewModel.stationList.observe(this) { result ->
            makeMarkersOnMap(result)
            adapter.submitList(result)
        }

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

        map.setMapViewEventListener(this)
        map.setCurrentLocationEventListener(this)
        map.setPOIItemEventListener(this)
    }

    private fun makeMarkersOnMap(stations: List<Station>) {
        for(station in stations) {
            val marker = MapPOIItem()
            marker.itemName = station.stationName
            marker.mapPoint = MapPoint.mapPointWithGeoCoord(station.latitude, station.longitude)
            marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
            marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            map.addPOIItem(marker)
        }
    }

    // 초기화
    private fun mapViewAndBottomSheetInit() {
        map = MapView(this)
        mapView.addView(map)
        map.setZoomLevelFloat(DEFAULT_ZOOM_LEVEL, false)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.halfExpandedRatio = 0.4f
        bottomSheetBehavior.isFitToContents = false
    }

    // 내 위치(gps가 켜져 있을 경우) 표시하고, 카메라 이동시켜주기
    private fun findMyLocationAndMoveCamera() {
        map.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        map.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(myLocationMapPoint, DEFAULT_ZOOM_LEVEL)))
    }

    /***
     * listener 구현
     * 보기 싫은데, 이렇게 안하면 오류가 발생함 (왜 그런지는 모르겠음)
     * ***/
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
        for((index, value) in mainViewModel.stationList.value!!.withIndex()) {
            if(value.stationName == item.itemName) {
                smoothScroller.targetPosition = index
                // resultAdapter.updateNotifyItemChanged(index)
                break
            }
        }
        bottomSheet.recycler.layoutManager?.startSmoothScroll(smoothScroller)
    }

    interface Callback {
        fun setShowingMethodViewStyle(selected: Int)
    }

}