package gymkapp.main.viewmodel.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import gymkapp.main.VALIDATION_DISTANCE
import gymkapp.main.api.RemoteAPI
import gymkapp.main.model.GeoJSONPoint
import gymkapp.main.model.MapPreview
import gymkapp.main.model.Point
import gymkapp.main.model.Stage
import kotlinx.coroutines.delay

class MapsFragmentModel : ViewModel() {

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
    DISABLED,
    STARTED,
    FINISHED //mostrar resultado final y tiempo
  }

  enum class PointStatus {
    CHECKING,
    POINT_ACHIEVED
  }


  private val classTag = javaClass.simpleName //TODO: borrar cuando acabemos

  val locationRequest: LocationRequest by lazy {
    LocationRequest().apply {
      interval = 10_000
      fastestInterval = 5_000
      smallestDisplacement = (VALIDATION_DISTANCE/10).toFloat() //un tanto absurdo. Cambiar cuando nos hayamos decidido
      priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
  }

  val locationCallback by lazy {
    object : LocationCallback() {
      override fun onLocationResult(locRes: LocationResult?) {
        locRes?.let { Log.d(classTag,"size: ${it.locations.size}") }
        currentLoc.value = locRes?.lastLocation
        isFirstTimeLocationRequest = false
      }

      override fun onLocationAvailability(availability: LocationAvailability?) {
        if (!isFirstTimeLocationRequest && !availability!!.isLocationAvailable) {
          Log.d(classTag, "Localizacion no disponible, comprobando...")
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
  val gameState = MutableLiveData(GameStatus.DISABLED)
  val pointState = MutableLiveData(PointStatus.CHECKING)

  var stage: Stage? = null

  suspend fun startGame() {

    val (_, aux) = RemoteAPI.obtainStartMap()
    aux?.let {

      stage = it
      gameState.value = GameStatus.STARTED
    }
  }

  fun forceVerification() {
    pointState.value = PointStatus.POINT_ACHIEVED
  }

  fun startChecking() {
    pointState.value = PointStatus.CHECKING
  }

  suspend fun verifyCurrentLocation() {

    val (_, aux) = RemoteAPI.obtainNextStageMap(
      Point(
        GeoJSONPoint(
          coordinates = listOf(
            currentLoc.value!!.longitude,
            currentLoc.value!!.latitude
          )
        )
      )
    )

    aux?.let {
      stage = aux
      pointState.value = PointStatus.POINT_ACHIEVED
    }
    //aux?.error? TODO??
  }

  /**
   * Mock function to return the list of near points
   */
  suspend fun nearGymkhanas(): List<MapPreview> {
    delay(1_000)
    return listOf(
      MapPreview(
        author = "Juan Sebastian",
        description = """
          Aventurate a ver las zonas mas emblemáticas de la ciudad de Barcelona!
          ¿Eres residente y aún no has visitado las maravillas de Barcelona? ¿Turista?
          Sea como se esta Gincana es para ti!!
        """.trimIndent(),
        name = "Paseo por la ciudad",
        startingPoint = LatLng(1.2332,123.23)
      ),
      MapPreview(
        author = "Pedro Jose",
        description = """
          Te gusta la historia?
          Esta Gymkhana te pondrá a prueba tu conocimiento sobre la historia moderna, y no tan moderna de la ciudad.
          Animate!
        """.trimIndent(),
        name = "Fans de la Historia",
        startingPoint = LatLng(1.323,1.12332)
      )
    )
  }

  fun startFollowing() {
    followingStatus.value =
      FollowingStatus.FOLLOWING
  }

  fun stopFollowing() {
    followingStatus.value =
      FollowingStatus.DISABLED
  }

  fun switchFollowing() {
    if (followingStatus.value == FollowingStatus.UNKNOWN) {
      Log.d(classTag, "Operacion incompatible")
      return
    }
    followingStatus.value =
      if (followingStatus.value == FollowingStatus.FOLLOWING) FollowingStatus.DISABLED else FollowingStatus.FOLLOWING
  }

  fun confirmLocationSettingsEnabled() {
    locationSettingStatus.value =
      LocationSettingsStatus.ENABLED
  }

  fun confirmLocationSettingsDenied() {
    locationSettingStatus.value =
      LocationSettingsStatus.DISABLED
  }

  /**
   * Crea el cliente de llamadas a la API por la parte de mapas antes de empezar a realizar llamadas a la API
   */
  fun createPrivateMapsApiClient(loginToken: String) {
    RemoteAPI.initMapsCallsClient(loginToken)
  }
}