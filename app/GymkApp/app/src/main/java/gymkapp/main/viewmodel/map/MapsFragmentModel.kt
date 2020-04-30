package gymkapp.main.viewmodel.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import gymkapp.main.api.RemoteAPI
import gymkapp.main.model.Stage

class MapsFragmentModel : ViewModel(){

  //TODO cambiar nombre cuando sepa si es activityViewModel o solo ViewModel
  enum class LocationSettingsStatus {
    ENABLED,
    CHECKING,
    DISABLED
  }

  enum class FollowingStatus {
    UNKNOWN,
    FOLLOWING,
    DISABLED
  }

    enum class GameStatus {
        UNKNOWN,
        CHECKING,
        STARTED,
        FINISHED
    }

    enum class PointStatus {
        CHECKING,
        POINT_ACHIEVED
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
        isFirstTimeLocationRequest = false
      }

      override fun onLocationAvailability(availability: LocationAvailability?) {
        if(!isFirstTimeLocationRequest && !availability!!.isLocationAvailable){
          Log.d(classTag,"Localizacion no disponible, comprobando...")
          locationSettingStatus.value =
            LocationSettingsStatus.CHECKING
        }
      }
    }
  }

  var isFirstTimePermissionFlow = true
  var isFirstTimeLocationRequest = true
  val locationSettingStatus = MutableLiveData(LocationSettingsStatus.CHECKING)
  val followingStatus = MutableLiveData(FollowingStatus.UNKNOWN)
  val currentLoc = MutableLiveData<Location?>(null)
  val gameState = MutableLiveData(GameStatus.UNKNOWN)
    val pointState = MutableLiveData(PointStatus.CHECKING)

     var stage: Stage? = null

  fun checkingGame(){
    gameState.value =
      GameStatus.CHECKING
  }

  fun startFollowing(){
    followingStatus.value =
      FollowingStatus.FOLLOWING
  }

  fun stopFollowing(){
    followingStatus.value =
      FollowingStatus.DISABLED
  }

  fun switchFollowing(){
    if (followingStatus.value== FollowingStatus.UNKNOWN){
      Log.d(classTag,"Operacion incompatible")
      return
    }
    followingStatus.value = if(followingStatus.value == FollowingStatus.FOLLOWING) FollowingStatus.DISABLED else FollowingStatus.FOLLOWING
  }

  fun confirmLocationSettingsEnabled(){
    locationSettingStatus.value =
      LocationSettingsStatus.ENABLED
  }

  fun confirmLocationSettingsDenied(){
    locationSettingStatus.value =
      LocationSettingsStatus.DISABLED
  }
    suspend fun llamadaObtenerFirstPoint() {

        var (message, aux) = RemoteAPI.obtainStartMap()
        stage = aux
        gameState.value = GameStatus.STARTED
        pointState.value = PointStatus.POINT_ACHIEVED //obtengo el primer punto
    }

    suspend fun llamadaVerifyPunto (): Boolean {
       //gameState.value = GameStatus.STARTED
        return true
    }

  /**
   * Crea el cliente de llamadas a la API por la parte de mapas antes de empezar a realizar llamadas a la API
   */
  fun createPrivateMapsApiClient(loginToken: String){
    RemoteAPI.initMapsCallsClient(loginToken)
  }
}