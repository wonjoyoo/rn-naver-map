package com.mjstudio.reactnativenavermap.mapview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper

import com.naver.maps.map.LocationSource

import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY

class QuickLocationSource(
  private val context: Context
) : LocationSource {
  private var listener: LocationSource.OnLocationChangedListener? = null
  private val fusedClient = LocationServices.getFusedLocationProviderClient(context)
  private val req = LocationRequest.create().apply {
    interval = 1000     // 1초
    fastestInterval = 500
    priority = PRIORITY_HIGH_ACCURACY
  }
  private val callback = object : LocationCallback() {
    override fun onLocationResult(result: LocationResult) {
      result.lastLocation?.let { listener?.onLocationChanged(it) }
    }
  }

  @SuppressLint("MissingPermission")
  override fun activate(listener: LocationSource.OnLocationChangedListener) {
    this.listener = listener
    fusedClient.lastLocation.addOnSuccessListener { loc ->
      loc?.let { listener.onLocationChanged(it) }
    }
    fusedClient.requestLocationUpdates(req, callback, Looper.getMainLooper())
  }

  override fun deactivate() {
    listener = null
    fusedClient.removeLocationUpdates(callback)
  }
}
