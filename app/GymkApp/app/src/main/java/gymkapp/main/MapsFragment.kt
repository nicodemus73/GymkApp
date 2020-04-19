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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import gymkapp.main.LoginViewModel.AuthenticationState.*

import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.ktx.MapsExperimentalFeature
import com.google.maps.android.ktx.awaitMap
import gymkapp.main.databinding.MapsBinding
import kotlinx.coroutines.launch
import java.lang.Exception

class MapsFragment : Fragment() {

  private val classTag = javaClass.simpleName //Solo para debugeo

  private val loginModel: LoginViewModel by activityViewModels()
  private lateinit var map: GoogleMap

  private lateinit var fusedLocationClient: FusedLocationProviderClient
  private lateinit var locationRequest: LocationRequest
  private lateinit var locationCallback: LocationCallback
  private var currentLoc: Location? = null
  private var recievingLocUpdates = false

  private var _bind: MapsBinding? = null
  private val bind: MapsBinding inline get() = _bind!!

  //TODO Flujo de permisos y settings
  //TODO llamada a la fucion para obtener puntos cercanos
  //TODO Cambiar el loginToken por el singleton del usuario en el loginViewModel
  //TODO Considerar utilizar un viewmodel para guardar estados del mapa o un savedinstancestate

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _bind = MapsBinding.inflate(inflater, container, false)
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

    map =
      (childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment)?.awaitMap() ?: return
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    map.uiSettings.isMyLocationButtonEnabled = false

    Log.d(classTag, map.maxZoomLevel.toString())
    if (ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      doAfterLocationGranted()
    } else {

      if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
        Snackbar.make(
          view,
          "Location permission is required to show near gymkhanas",
          Snackbar.LENGTH_LONG
        ).show()
      }
      requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
    }
    bind.locationButton.setOnClickListener {

      currentLoc?.run {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 10F))
      }
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {

    if (requestCode == LOCATION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      Log.d(classTag, "Activando la localizacion")
      doAfterLocationGranted()
    }
  }

  private fun doAfterLocationGranted() {

    map.isMyLocationEnabled = true
    bind.locationButton.show()
    createLocationRequest()
    checkSettings()
  }

  private fun createLocationRequest() {

    locationRequest = LocationRequest().apply {
      interval = 10_000
      fastestInterval = 1_000
      priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
  }

  /**
   * Comprueba si el usuario tiene activada la localizacion
   * Solo se comprueba al iniciar el fragmento
   */
  private fun checkSettings() {

    val builder = LocationSettingsRequest.Builder()
      .addLocationRequest(locationRequest)
    val client = LocationServices.getSettingsClient(requireContext())
    val task = client.checkLocationSettings(builder.build())
    task.addOnSuccessListener {

      Log.d(classTag, "check settings SUCCESSFUL")
      locationCallback = object : LocationCallback() {
        override fun onLocationResult(locRes: LocationResult?) {

          Log.d(
            classTag,
            "Recibiendo actualizacion Lat Long: ${currentLoc?.latitude},${currentLoc?.longitude} -> ${locRes?.lastLocation?.latitude}, ${locRes?.lastLocation?.longitude}"
          )
          currentLoc = locRes?.lastLocation
        }
      }
      recievingLocUpdates = true
      fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
      )
    }

    task.addOnFailureListener {
      Log.d(classTag, "check settings FAILED")
      //TODO a lo mejor no mostrar la ultima localizacion si esto falla
    }
  }

  override fun onPause() {
    super.onPause()
    if (recievingLocUpdates) fusedLocationClient.removeLocationUpdates(locationCallback)
      .also { recievingLocUpdates = false }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _bind = null
  }
}