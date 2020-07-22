package com.dongldh.fike

import android.graphics.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dongldh.fike.util.DEFAULT_ZOOM_LEVEL
import kotlinx.android.synthetic.main.activity_main.*
import net.daum.mf.map.api.CameraPosition
import net.daum.mf.map.api.CameraUpdateFactory
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val map = MapView(this)
        mapView.addView(map)

        myLocationFab.setOnClickListener {
            map.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        }

        map.setCurrentLocationEventListener(object: MapView.CurrentLocationEventListener {
            override fun onCurrentLocationUpdateFailed(p0: MapView?) {
                Toast.makeText(this@MainActivity, "현재 위치 갱신작업에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }

            // 음.. 위치가 바뀔때마다 안의 식이 동작하네..?!
            override fun onCurrentLocationUpdate(p0: MapView?, point: MapPoint?, p2: Float) {
                map.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(point, DEFAULT_ZOOM_LEVEL)))
            }

            override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
                Toast.makeText(this@MainActivity, "취소", Toast.LENGTH_SHORT).show()
            }

            override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {
                Toast.makeText(this@MainActivity, "단말각도 제공", Toast.LENGTH_SHORT).show()
            }
        })
    }

}