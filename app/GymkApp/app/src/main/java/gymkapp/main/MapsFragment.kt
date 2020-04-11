package gymkapp.main

import android.content.Context
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import gymkapp.main.LoginViewModel.AuthenticationState.*

import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.addMarker
import kotlinx.android.synthetic.main.maps.view.*
import java.lang.Exception

class MapsFragment : Fragment() {

  /*private val callback = OnMapReadyCallback { googleMap ->
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    val barcelona = LatLng(41.404423, 2.174071)
    googleMap.addMarker{
      position(barcelona)
      title("Marker in Barcelona")
    }
  }*/

  private val loginModel: LoginViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.maps, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
    mapFragment?.getMapAsync { map ->
      val barcelona = LatLng(41.404423, 2.174071)
      map.addMarker{
        position(barcelona)
        title("Marker in Barcelona")
      }
    }

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