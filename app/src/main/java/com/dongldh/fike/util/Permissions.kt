package com.dongldh.fike.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import com.dongldh.fike.google.GoogleMapControl
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

@SuppressLint("MissingPermission")
class Permissions(val context: Context, val mMap: GoogleMap) {
    // GPS 위치정보를 사용하는 퍼미션 승인 여부 체크
    fun permissionLocation() {
        val locationPermissionListener = object: PermissionListener {
            override fun onPermissionGranted() {
                mMap.isMyLocationEnabled = true
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GoogleMapControl(context).getMyLocation(), DEFAULT_ZOOM_LEVEL))
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                val CITY_HALL =  LatLng(37.5662952, 126.97794509999994)
                mMap.isMyLocationEnabled = false
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CITY_HALL, DEFAULT_ZOOM_LEVEL))
            }

        }

        TedPermission.with(context)
            .setPermissionListener(locationPermissionListener)
            .setRationaleMessage("To find your nearest location, we need 'location permission'")
            .setDeniedMessage("If you reject permission, you cannot find your nearest location automatically\n\n" +
                "Please turn on permission at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            .setGotoSettingButton(true)
            .setGotoSettingButtonText("Go to setting")
            .check()
    }
}