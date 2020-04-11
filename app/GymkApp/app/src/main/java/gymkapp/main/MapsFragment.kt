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
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import gymkapp.main.LoginViewModel.AuthenticationState.*

import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.ktx.addMarker
import kotlinx.android.synthetic.main.maps.view.*
import java.lang.Exception

const val REQUEST_CODE = 3

class MapsFragment : Fragment() {

  private val loginModel: LoginViewModel by activityViewModels()
  private lateinit var map: GoogleMap

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.maps, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initialChecks()

    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
    mapFragment?.getMapAsync {

      map = it
      if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
        map.isMyLocationEnabled = true
      } else{

        if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
          Snackbar.make(view,"Location permission is required to show near gymkhanas",Snackbar.LENGTH_SHORT).show()
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
      }
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {

    if(requestCode == REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
      map.isMyLocationEnabled = true
  }

  fun initialChecks(){

    val navController = findNavController()

    if(loginModel.authenticationState.value==INVALID_AUTHENTICATION){

      Log.d(javaClass.simpleName,"Leyendo el disco")
      loginModel.authenticate(
        try {
          activity?.getPreferences(Context.MODE_PRIVATE)?.getString(R.string.TokenKey.toString(), null)
        } catch (e: Exception) {
          Log.d(javaClass.simpleName,"Error al intentar leer el disco")
          null
        }
      )
    }

    loginModel.authenticationState.observe(viewLifecycleOwner, Observer {
      if (it == UNAUTHENTICATED){
        Log.d(javaClass.simpleName, "No autenticado, yendo a la pantalla login")
        navController.navigate(MapsFragmentDirections.toLoginFTUE())
      }
    })
  }
}