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
import gymkapp.main.model.GeoJSONPoint
import gymkapp.main.model.Point
import gymkapp.main.model.Stage
import gymkapp.main.toLatLng

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
        STARTED, //El juego esta activo puede ser cualquier punto del mapa
        FINISHED //mostrar resultado final y tiempo
    }

    enum class PointStatus {
        UNKNOWN,
        CHECKING,
        SERVERCALL,
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
  val gameState = MutableLiveData(GameStatus.STARTED)
    val pointState = MutableLiveData(PointStatus.POINT_ACHIEVED)

     var stage: Stage? = null

  fun checkingGame(){
    gameState.value =
      GameStatus.CHECKING
  }

  fun checkingPoint() {
    pointState.value = PointStatus.CHECKING
  }
  fun serverPointCallPoint() {
    pointState.value = PointStatus.SERVERCALL
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
  /**
   * funcion para inciar el juego estes donde estes
   * te da el primer punto donde dirigirse
   * no verifica
   */
    suspend fun llamadaObtenerFirstPoint() {

        val (message, aux) = RemoteAPI.obtainStartMap() //cambiar a startgamemap
        stage = aux
        gameState.value = GameStatus.STARTED
        pointState.value = PointStatus.POINT_ACHIEVED //obtengo el primer punto
    }

    suspend fun llamadaVerifyPunto () {

        val point = Point(GeoJSONPoint(coordinates =  listOf(currentLoc.value!!.longitude, currentLoc.value!!.latitude ) ))
        val (message, aux) = RemoteAPI.obtainNextStageMap(point)
        if (aux?.error != null) {
          stage = aux
          pointState.value = PointStatus.POINT_ACHIEVED
        }else
          checkingPoint()

    }

  /**
   * Crea el cliente de llamadas a la API por la parte de mapas antes de empezar a realizar llamadas a la API
   */
  fun createPrivateMapsApiClient(loginToken: String){
    RemoteAPI.initMapsCallsClient(loginToken)
  }
}