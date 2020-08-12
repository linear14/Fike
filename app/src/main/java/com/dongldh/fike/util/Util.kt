package com.dongldh.fike.util

import android.location.Location

object Util {
    // 위경도에 따른 직선 거리 계산
    fun distanceByDegree(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val start = Location("A")
        val end = Location("B")

        start.latitude = lat1
        start.longitude = lon1
        end.latitude = lat2
        end.longitude = lon2

        return start.distanceTo(end).toDouble()
    }
}