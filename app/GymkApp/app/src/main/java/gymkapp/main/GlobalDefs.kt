package gymkapp.main

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.ktx.utils.withSphericalOffset

//Map Fragment
const val PERMISSION_SETTINGS_REQ_CODE = 444
const val LOCATION_SETTINGS_REQ_CODE = 445
const val LOCATION_REQUEST_CODE = 556
const val DEFAULT_VIEW_RADIUS = 500 //Radio a utilizar para ver los puntos cercanos

//Server
const val BASE_URL = "https://10.4.41.144:3002"

//Login & Register validation
const val USER_MIN_LENGTH = 3
const val USER_MAX_LENGTH = 20

//Map naming validation
const val MAP_NAME_MIN_LENGTH = 3
const val MAP_NAME_MAX_LENGTH = 20
const val MAP_DESCRIPTION_MIN_LENGTH = 30
const val MAP_DESCRIPTION_MAX_LENGTH = 150

//Game comment validation
const val GAME_COMMENT_MIN_LENGTH = 1
const val GAME_COMMENT_MAX_LENGTH = 150

fun Location.toLatLng() = LatLng(latitude, longitude)

fun LatLng.createBounds(radius: Int): LatLngBounds = LatLngBounds
  .builder()
  .include(withSphericalOffset(radius.toDouble(), 0.0))
  .include(withSphericalOffset(radius.toDouble(), 90.0))
  .include(withSphericalOffset(radius.toDouble(), 180.0))
  .include(withSphericalOffset(radius.toDouble(), 270.0))
  .build()