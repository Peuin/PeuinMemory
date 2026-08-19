package com.example.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val fullAddress: String,
    val accuracyMeters: Float = 0f,
    val isRealGps: Boolean = true
)

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val finePermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarsePermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return finePermission || coarsePermission
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): UserLocation? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission()) {
            return@withContext null
        }

        try {
            val location: Location? = suspendCancellableCoroutine { continuation ->
                val cancellationToken = CancellationTokenSource()
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                ).addOnSuccessListener { loc ->
                    if (loc != null) {
                        continuation.resume(loc)
                    } else {
                        // Fallback to last known location
                        fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                            if (lastLoc != null) {
                                continuation.resume(lastLoc)
                            } else {
                                // Fallback to system LocationManager
                                val systemLoc = getSystemLocationFallback()
                                continuation.resume(systemLoc)
                            }
                        }.addOnFailureListener {
                            continuation.resume(getSystemLocationFallback())
                        }
                    }
                }.addOnFailureListener {
                    continuation.resume(getSystemLocationFallback())
                }

                continuation.invokeOnCancellation {
                    cancellationToken.cancel()
                }
            }

            if (location != null) {
                val (city, address) = reverseGeocode(location.latitude, location.longitude)
                UserLocation(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    cityName = city,
                    fullAddress = address,
                    accuracyMeters = location.accuracy,
                    isRealGps = true
                )
            } else {
                // Default fallback to Đà Lạt center coordinates for seamless preview
                UserLocation(
                    latitude = 11.9404,
                    longitude = 108.4583,
                    cityName = "Đà Lạt",
                    fullAddress = "Phường 1, TP. Đà Lạt, Lâm Đồng",
                    accuracyMeters = 10f,
                    isRealGps = false
                )
            }
        } catch (e: Exception) {
            UserLocation(
                latitude = 11.9404,
                longitude = 108.4583,
                cityName = "Đà Lạt",
                fullAddress = "TP. Đà Lạt, Lâm Đồng",
                accuracyMeters = 15f,
                isRealGps = false
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getSystemLocationFallback(): Location? {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val gpsLoc = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val netLoc = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val passiveLoc = locationManager?.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            gpsLoc ?: netLoc ?: passiveLoc
        } catch (e: Exception) {
            null
        }
    }

    @Suppress("DEPRECATION")
    private fun reverseGeocode(latitude: Double, longitude: Double): Pair<String, String> {
        return try {
            val geocoder = Geocoder(context, Locale.forLanguageTag("vi-VN"))
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val city = addr.adminArea ?: addr.subAdminArea ?: addr.locality ?: "Việt Nam"
                val full = addr.getAddressLine(0) ?: "$city, Việt Nam"
                Pair(city, full)
            } else {
                getFallbackCity(latitude, longitude)
            }
        } catch (e: Exception) {
            getFallbackCity(latitude, longitude)
        }
    }

    private fun getFallbackCity(lat: Double, lng: Double): Pair<String, String> {
        // Spatial heuristic bounding boxes for Vietnam key destinations
        return when {
            lat in 11.7..12.2 && lng in 108.2..108.7 -> Pair("Đà Lạt", "TP. Đà Lạt, Lâm Đồng")
            lat in 10.6..11.0 && lng in 106.5..107.0 -> Pair("TP. Hồ Chí Minh", "Quận 1, TP. Hồ Chí Minh")
            lat in 20.8..21.3 && lng in 105.6..106.1 -> Pair("Hà Nội", "Hoàn Kiếm, Hà Nội")
            lat in 15.9..16.2 && lng in 108.1..108.4 -> Pair("Đà Nẵng", "Hải Châu, Đà Nẵng")
            lat in 15.8..16.0 && lng in 108.3..108.5 -> Pair("Hội An", "Phố Cổ Hội An, Quảng Nam")
            lat in 10.0..10.5 && lng in 103.8..104.2 -> Pair("Phú Quốc", "Dương Đông, Phú Quốc, Kiên Giang")
            lat in 22.2..22.5 && lng in 103.7..104.0 -> Pair("Sa Pa", "Sa Pa, Lào Cai")
            else -> Pair("Đà Lạt", "Đà Lạt, Lâm Đồng")
        }
    }
}
