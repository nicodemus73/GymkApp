package gymkapp.main

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class MapsFragment : Fragment() {
  //TODO Valor por defecto de authenticate -> AUTHENTICATED (o deberia)

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
    val sydney = LatLng(-34.0, 151.0)
    googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
    googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
  }*/

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.maps, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    Log.d(javaClass.name,"Me han creado")
    //super.onViewCreated(view, savedInstanceState)
    //val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
    //mapFragment?.getMapAsync(callback)

    val navController = findNavController()
  }
}