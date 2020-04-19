package gymkapp.main

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.ktx.MapsExperimentalFeature
import com.google.maps.android.ktx.awaitMap
import gymkapp.main.LoginViewModel.AuthenticationState.*
import gymkapp.main.databinding.MapsBinding
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MapsFragment : Fragment() {

  private val classTag = javaClass.simpleName //Solo para debugeo

  private val loginModel: LoginViewModel by viewModels()
  private val mapsModel: MapsFragmentModel by activityViewModels()
  private lateinit var map: GoogleMap

  private lateinit var fusedLocationClient: FusedLocationProviderClient
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

    /*mapsModel.locationPermissionStatus.observe(viewLifecycleOwner, Observer { status ->
      when(status!!){

        PermStatus.UNKNOWN -> {

          if(ContextCompat.checkSelfPermission(
              requireContext(),
              Manifest.permission.ACCESS_FINE_LOCATION
            )== PackageManager.PERMISSION_GRANTED)
          {
            mapsModel.allowLocationPermission() //TODO Caso en el que se active la localizacion antes de encender la app?? Por si acaso...
          }
          requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_REQUEST_CODE)
        }
        PermStatus.ALLOWED -> doAfterLocationGranted()
        PermStatus.DENIED -> {

          if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
            //Sustituir por un banner que permita volver aceptar el permiso de localizacion
            Snackbar.make(view,"Location is required to show near gymkhanas",Snackbar.LENGTH_LONG).show()
            //requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
          } else mapsModel.disableLocationPermission()
        }
        MapsFragmentModel.LocationPermissionStatus.DISABLED -> {
          //Mostrar un banner que permita ir a los ajustes y activar el permiso
          //Nota: pedir permiso de nuevo no funcionaria... Es obligado mandarlo a los ajustes para facilitarlo
        }
      }
    })*/
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {

    EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
  }

  @AfterPermissionGranted(LOCATION_REQUEST_CODE)
  private fun doAfterLocationGranted() {

    map.isMyLocationEnabled = true
    bind.locationButton.show()
    bind.locationButton.setOnClickListener {

      mapsModel.currentLoc?.run {

        //TODO añadir algo de padding o aumentar el radio en x metros
        map.animateCamera(
          CameraUpdateFactory.newLatLngBounds(
            this.toLatLng().createBounds(
              DEFAULT_VIEW_RADIUS
            ), 0
          )
        )
      }
    }
    checkSettings()
  }

  /**
   * Comprueba si el usuario tiene activada la localizacion
   */
  private fun checkSettings() {

    val builder = LocationSettingsRequest.Builder()
      .addLocationRequest(mapsModel.locationRequest)
    val client = LocationServices.getSettingsClient(requireContext())
    val task = client.checkLocationSettings(builder.build())
    task.addOnSuccessListener {

      Log.d(classTag, "check settings SUCCESSFUL")
      recievingLocUpdates = true
      fusedLocationClient.requestLocationUpdates(
        mapsModel.locationRequest,
        mapsModel.locationCallback,
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
    if (recievingLocUpdates) {
      fusedLocationClient.removeLocationUpdates(mapsModel.locationCallback)
      recievingLocUpdates = false
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _bind = null
  }
}