package gymkapp.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import gymkapp.main.LoginViewModel.AuthenticationState.*

import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.ktx.MapsExperimentalFeature
import com.google.maps.android.ktx.awaitMap
import gymkapp.main.databinding.MapsBinding
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.TimeUnit

const val REQUEST_CODE = 3

class MapsFragment : Fragment() {

  private val classTag = javaClass.simpleName
  //TODO Al aceptar acceso de localizacion no funciona hasta recargarse

  private val loginModel: LoginViewModel by activityViewModels()
  private lateinit var map: GoogleMap

  private lateinit var fusedLocationClient: FusedLocationProviderClient
  private lateinit var locationRequest: LocationRequest
  private lateinit var locationCallback: LocationCallback
  private var currentLoc: Location? = null

  private var _bind : MapsBinding? = null
  private val bind: MapsBinding get() = _bind!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _bind = MapsBinding.inflate(inflater,container,false)
    return bind.root
  }

  @MapsExperimentalFeature
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initialChecks(view)
  }

  @MapsExperimentalFeature
  private fun initialChecks(view: View) {

    val navController = findNavController()

    if (loginModel.authenticationState.value == INVALID_AUTHENTICATION) {

      Log.d(classTag, "Leyendo el disco")
      loginModel.authenticate(
        try {
          activity?.getPreferences(Context.MODE_PRIVATE)
            ?.getString(R.string.TokenKey.toString(), null)
        } catch (e: Exception) {
          Log.d(classTag, "Error al intentar leer el disco")
          null
        }
      )
    }

    loginModel.authenticationState.observe(viewLifecycleOwner, Observer {
      if (it == UNAUTHENTICATED) {
        Log.d(classTag, "No autenticado, yendo a la pantalla login")
        navController.navigate(MapsFragmentDirections.toLoginFTUE())
      } else if (it == AUTHENTICATED) lifecycleScope.launch { startMaps(view) }
    })
  }

  @MapsExperimentalFeature
  private suspend fun startMaps(view: View) {

    map = (childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment)?.awaitMap() ?: return
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    if (ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      map.isMyLocationEnabled = true
    } else {

      if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
        Snackbar.make(
          view,
          "Location permission is required to show near gymkhanas",
          Snackbar.LENGTH_LONG
        ).show()
      }
      requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
    }

    map.setOnMyLocationButtonClickListener {

      fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
        Log.d(classTag,"localizacion ${if(map.isMyLocationEnabled) "activada" else "desactivada"}")
        loc?.run {
          Snackbar.make(
            view,
            "Your location: Lat: $latitude, Long: $longitude",
            Snackbar.LENGTH_LONG
          ).setAction("Ignore"){}.show()
        } ?: Log.d(classTag, "Localizacion no disponible")
      }
      false
    }
  }

  private fun createLocationRequest() {
    locationRequest = LocationRequest().apply {
      interval = 10000
      fastestInterval = 1000
      priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {

    if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      Log.d(classTag, "Activando la localizacion")
      map.isMyLocationEnabled = true
      fusedLocationClient.lastLocation.addOnSuccessListener { currentLoc = it } //get the last known location
      createLocationRequest()
      val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
      val client = LocationServices.getSettingsClient(requireContext())
      val task = client.checkLocationSettings(builder.build())
      task.addOnSuccessListener {

        Log.d(classTag,"Acaba la tarea exitosamente")
        locationCallback = object : LocationCallback(){
          override fun onLocationResult(locRes: LocationResult?) {
            Log.d(classTag,"He obtenido un resultado")
            locRes ?: return
            currentLoc = locRes.lastLocation
            for(loc in locRes.locations){
              Log.d(classTag,"localizacion: ${loc.latitude},${loc.longitude}")
              Log.d(classTag,"ultima posicion: ${currentLoc?.latitude},${currentLoc?.longitude}")
            }
          }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _bind = null
  }
}