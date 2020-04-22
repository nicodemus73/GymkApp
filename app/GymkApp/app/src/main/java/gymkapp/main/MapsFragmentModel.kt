package gymkapp.main

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class MapsFragmentModel : ViewModel(){

  enum class LocationSettingsStatus {
    ENABLED,
    DISABLED,
    UNKNOWN
  }

  enum class FollowingStatus {
    UNKNOWN,
    FOLLOWING,
    DISABLED
  }

  private val classTag = javaClass.simpleName //TODO: borrar cuando acabemos

  val locationRequest: LocationRequest by lazy {
    LocationRequest().apply {
      interval = 10_000
      fastestInterval = 2_000
      priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    } //TODO smallestDisplacement - distancia minima para avisar de cambios
  }

  val locationCallback by lazy {
    object: LocationCallback(){
      override fun onLocationResult(locRes: LocationResult?) {
        currentLoc.value = locRes?.lastLocation
      }

      override fun onLocationAvailability(availability: LocationAvailability?) {
        //Log.d(classTag, "disponible: ${availability!!.isLocationAvailable}")
        if(!availability!!.isLocationAvailable){
          Log.d(classTag,"Localizacion no disponible")
          locationSettingStatus.value = LocationSettingsStatus.DISABLED
        } else locationSettingStatus.value = LocationSettingsStatus.ENABLED
      }
    }
  }

  var isFirstTimePermissionFlow = true
  val locationSettingStatus = MutableLiveData(LocationSettingsStatus.UNKNOWN)
  val followingStatus = MutableLiveData(FollowingStatus.UNKNOWN)
  val currentLoc = MutableLiveData<Location?>(null)

  fun startFollowing(){
    followingStatus.value = FollowingStatus.FOLLOWING
  }

  fun stopFollowing(){
    followingStatus.value = FollowingStatus.DISABLED
  }

  fun switchFollowing(){
    if (followingStatus.value==FollowingStatus.UNKNOWN){
      Log.d(classTag,"Operacion incompatible")
      return
    }
    followingStatus.value = if(followingStatus.value == FollowingStatus.FOLLOWING)FollowingStatus.DISABLED else FollowingStatus.FOLLOWING
  }
}