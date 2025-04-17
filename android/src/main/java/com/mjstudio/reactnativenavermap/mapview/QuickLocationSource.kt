package com.mjstudio.reactnativenavermap.mapview

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.naver.maps.map.LocationSource

/**
 * NaverMap 에 “즉시 캐시된 위치 + 실시간 GPS” 를 공급하는 커스텀 LocationSource
 */
class QuickLocationSource(
  private val context: Context,
  private var minTime: Long = 5000L,
  private var minDistance: Float = 10f,

) : LocationSource, LocationListener {

  private var listener: LocationSource.OnLocationChangedListener? = null
  private val locationManager =
    context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

  @SuppressLint("MissingPermission")
  override fun activate(listener: LocationSource.OnLocationChangedListener) {
    this.listener = listener

    // 1) 캐시된 마지막 위치가 있으면 즉시 던져줌
    locationManager
      ?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
      ?.let { listener.onLocationChanged(it) }

    // 2) 그 다음부터 실시간 업데이트 요청
    locationManager?.requestLocationUpdates(
      LocationManager.GPS_PROVIDER,
      minTime,
      minDistance,
      this
    )
  }

  override fun deactivate() {
    listener = null
    locationManager?.removeUpdates(this)
  }

  // LocationListener 구현
  override fun onLocationChanged(location: Location) {
    listener?.onLocationChanged(location)
  }
  override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
  override fun onProviderEnabled(provider: String) {}
  override fun onProviderDisabled(provider: String) {}
  
  // 런타임에 호출할 수 있는 업데이트 메서드
  fun updateRequestParams(time: Long, distance: Float) {
    // deactivate 된 뒤 다시 activate 될 때 새로운 값 사용하게 변경
    minTime = time
    minDistance = distance
  }
}
