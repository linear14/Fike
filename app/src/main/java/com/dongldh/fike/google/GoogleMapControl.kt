package com.dongldh.fike.google

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.widget.Toast

import com.dongldh.fike.util.DEFAULT_ZOOM_LEVEL
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng

class GoogleMapControl(val context: Context) {

    @SuppressLint("MissingPermission")
    fun getMyLocation(): LatLng {
        val locationProvider: String = LocationManager.GPS_PROVIDER
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val lastKnownLocation: Location = locationManager.getLastKnownLocation(locationProvider)!!

        return LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
    }

    fun onMyLocationButtonClick() {
        /*when {
            hasPermissions() -> {
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(getMyLocation(), DEFAULT_ZOOM_LEVEL))
            }
            else -> {
                Toast.makeText(applicationContext, "위치 사용권한 설정에 동의해주세요", Toast.LENGTH_LONG).show()
            }
        }*/
    }

}