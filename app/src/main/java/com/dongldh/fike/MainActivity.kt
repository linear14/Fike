package com.dongldh.fike

import android.graphics.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dongldh.fike.util.DEFAULT_ZOOM_LEVEL
import com.dongldh.fike.util.Hash
import com.dongldh.fike.util.Permissions
import kotlinx.android.synthetic.main.activity_main.*
import net.daum.mf.map.api.CameraPosition
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

// 인터페이스를 이런식으로 액티비티 내에서 직접 구현해서 사용해야하네... object로 넣어주면 먹히지가 않음..ㅋㅋ
class MainActivity : AppCompatActivity(), MapView.MapViewEventListener, MapView.CurrentLocationEventListener {
    var myLocationMapPoint: MapPoint? = null
    lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Permissions(this).permissionLocation()
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
    }

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