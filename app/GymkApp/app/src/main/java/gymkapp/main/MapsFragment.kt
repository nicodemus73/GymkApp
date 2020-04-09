package gymkapp.main

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

  //TODO Valor por defecto de authenticate -> AUTHENTICATED (o deberia)
  private val callback = OnMapReadyCallback { googleMap ->
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
    googleMap.addMarker(MarkerOptions().position(barcelona).title("Marker in Barcelona"))
    googleMap.moveCamera(CameraUpdateFactory.newLatLng(barcelona))
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.maps, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    Log.d(javaClass.name,"Me han creado")
    super.onViewCreated(view, savedInstanceState)
    val mapFragment = childFragmentManager.findFragmentById(R.id.map_container) as SupportMapFragment?
    mapFragment?.getMapAsync(callback)

    val navController = findNavController()
  }
}