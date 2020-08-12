package com.dongldh.fike

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
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


// 인터페이스를 이런식으로 액티비티 내에서 직접 구현해서 사용해야하네... object로 넣어주면 먹히지가 않음..ㅋㅋ
class MainActivity : AppCompatActivity(), MapView.MapViewEventListener, MapView.CurrentLocationEventListener, MapView.POIItemEventListener {

    private val mainViewModel: MainViewModel by viewModels { InjectorUtils.provideMainViewModelFactory(this) }

    lateinit var map: MapView
    var myLocationMapPoint: MapPoint? = null

    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    var nowResultStyle = 0 // 0 이면 거리순, 1 이면 남은 자전거순

    lateinit var resultAdapter: ResultAdapter

    // var stationRepository: StationRepository? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.apply {
            callback = object: Callback {
                override fun setShowingMethodViewStyle(selected: Int) {
                    nowResultStyle = selected
                    setShowingMethodStyle(
                        if(selected == 0) bottomSheet.show_distance else bottomSheet.show_remain_bikes,
                        if(selected == 0) bottomSheet.show_remain_bikes else bottomSheet.show_distance
                    )
                    // if(nowResultStyle == 0) listOrderingByDistance(selectedPairList) else listOrderingByRemainingBikes(selectedPairList)
                }
            }
        }
        Permissions(this).permissionLocation()
        mapViewAndBottomSheetInit()
        // Hash(this).getAppKeyHash()

        myLocationFab.setOnClickListener {
            findMyLocationAndMoveCamera()
        }

        findBikeFab.setOnClickListener {
            map.removeAllPOIItems()
            mainViewModel.getStationListFromRetrofit(
                myLocationMapPoint?.mapPointGeoCoord?.latitude ?: map.mapCenterPoint.mapPointGeoCoord.latitude,
                myLocationMapPoint?.mapPointGeoCoord?.longitude ?: map.mapCenterPoint.mapPointGeoCoord.longitude
            ) // gps 켜져있으면 내 위치 기준. 안 켜져 있으면 지도 중심 기준
        }

        mainViewModel.stationList.observe(this) { result ->
            for(station in result) {
                val marker = MapPOIItem()
                marker.itemName = station.stationName
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(station.latitude, station.longitude)
                marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
                marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                map.addPOIItem(marker)
            }
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

    // 초기화
    private fun mapViewAndBottomSheetInit() {
        map = MapView(this)
        mapView.addView(map)
        map.setZoomLevelFloat(DEFAULT_ZOOM_LEVEL, false)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.halfExpandedRatio = 0.4f
        bottomSheetBehavior.isFitToContents = false
    }

    // bottomSheet의 보는 방법 버튼을 눌렀을 때 디자인 바꿔주는 메서드
    private fun setShowingMethodStyle(selectedLayout: TextView, unselectedLayout: TextView) {
        selectedLayout.setTextColor(Color.WHITE)
        selectedLayout.setBackgroundResource(R.drawable.selected_box)
        unselectedLayout.setTextColor(Color.DKGRAY)
        unselectedLayout.setBackgroundResource(R.drawable.not_selected_box)
    }

    // 내 위치(gps가 켜져 있을 경우) 표시하고, 카메라 이동시켜주기
    private fun findMyLocationAndMoveCamera() {
        map.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        map.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(myLocationMapPoint, DEFAULT_ZOOM_LEVEL)))
    }

    // 어댑터 설정 다시하기 + bottomSheet 크기조절
    private fun changeRecyclerViewStateOnUiThread(list: MutableList<Pair<Station, Double>>) {
        runOnUiThread() {
            // resultAdapter = ResultAdapter(context = this, list = list, map = map, bottomSheetBehavior = bottomSheetBehavior, recycler = recycler)
            bottomSheet.recycler.layoutManager = LinearLayoutManager(this)
            bottomSheet.recycler.adapter = resultAdapter
            if(bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
    }

    // 거리순 리스트 정렬
    private fun listOrderingByDistance(list: MutableList<Pair<Station, Double>>) {
        list.sortBy { it.second }
        changeRecyclerViewStateOnUiThread(list)
    }

    // 남은 자전거 대수순 리스트 정렬
    private fun listOrderingByRemainingBikes(list: MutableList<Pair<Station, Double>>) {
        list.sortByDescending { it.first.parkingBikeTotCnt }
        changeRecyclerViewStateOnUiThread(list)
    }



    // 아 보기 싫다.. 아래부분 예쁘게 다른 클래스에서 처리할 수 없나..
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
        /*for(index in 0 until selectedPairList.size) {
            if(selectedPairList[index].first.stationName == item.itemName) {
                smoothScroller.targetPosition = index
                // resultAdapter.updateNotifyItemChanged(index)
                break
            }
        }*/
        bottomSheet.recycler.layoutManager?.startSmoothScroll(smoothScroller)

    }

    interface Callback {
        fun setShowingMethodViewStyle(selected: Int)
    }

}