package gymkapp.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import gymkapp.main.LoginViewModel.AuthenticationState.*

import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

const val REQUEST_CODE = 3

class MapsFragment : Fragment() {

  //TODO Al aceptar acceso de localizacion no funciona hasta recargarse

  private val loginModel: LoginViewModel by activityViewModels()
  private lateinit var map: GoogleMap
  private lateinit var fusedLocationClient: FusedLocationProviderClient

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.maps, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initialChecks(view)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {

    if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
      Log.d(javaClass.simpleName,"Activando la localizacion")
      map.isMyLocationEnabled = true
    }
  }

  private fun initialChecks(view: View) {

    val navController = findNavController()

    if (loginModel.authenticationState.value == INVALID_AUTHENTICATION) {

      Log.d(javaClass.simpleName, "Leyendo el disco")
      loginModel.authenticate(
        try {
          activity?.getPreferences(Context.MODE_PRIVATE)
            ?.getString(R.string.TokenKey.toString(), null)
        } catch (e: Exception) {
          Log.d(javaClass.simpleName, "Error al intentar leer el disco")
          null
        }
      )
    }

    loginModel.authenticationState.observe(viewLifecycleOwner, Observer {
      if (it == UNAUTHENTICATED) {
        Log.d(javaClass.simpleName, "No autenticado, yendo a la pantalla login")
        navController.navigate(MapsFragmentDirections.toLoginFTUE())
      } else if(it==AUTHENTICATED) startMaps(view)
    })
  }

  private fun startMaps(view: View){

    (childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment)?.getMapAsync {

      map = it
      fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

      if (ContextCompat.checkSelfPermission(
          requireContext(),
          Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
      ) {
        map.isMyLocationEnabled = true
      } else {

        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
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
          loc?.run {
            Snackbar.make(view,"Your location: Lat: $latitude, Long: $longitude",Snackbar.LENGTH_LONG).show()
          } ?: Log.d(javaClass.simpleName,"Localizacion no disponible")
        }
        false
      }
    }
  }
}