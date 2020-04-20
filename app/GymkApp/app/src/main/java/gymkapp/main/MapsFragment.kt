package gymkapp.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.ktx.MapsExperimentalFeature
import com.google.maps.android.ktx.awaitMap
import gymkapp.main.LoginViewModel.AuthenticationState.*
import gymkapp.main.databinding.MapsBinding
import kotlinx.coroutines.launch

class MapsFragment : Fragment() {

  private val classTag = javaClass.simpleName //TODO: borrar cuando acabemos

  private val mapsModel: MapsFragmentModel by activityViewModels() //TODO era necesario activityScope?
  private val loginModel: LoginViewModel by activityViewModels()
  private lateinit var map: GoogleMap

  private lateinit var fusedLocationClient: FusedLocationProviderClient

  private var _bind: MapsBinding? = null
  private val bind: MapsBinding inline get() = _bind!!

  private val enabledColor by lazy {
    ColorStateList.valueOf(Color.parseColor(enabledColorHexString))
  }
  private val disabledColor by lazy {
    ColorStateList.valueOf(Color.parseColor(disabledColorHexString))
  }
  private var isFirstLoc = true

  private companion object {
    private const val disabledColorHexString = "#211A51"
    private const val enabledColorHexString = "#FFFFFF"
  }

  //TODO Flujo de settings
  //TODO llamada a la fucion para obtener puntos cercanos
  //TODO Cambiar el loginToken por el singleton del usuario en el loginViewModel
  //TODO Considerar utilizar un savedinstancestate

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
    initialChecks()
  }

  @MapsExperimentalFeature
  private fun initialChecks() {

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
      } else if (it == AUTHENTICATED) lifecycleScope.launch { startMaps() }
    })
  }

  @MapsExperimentalFeature
  private suspend fun startMaps() {

    map =
      (childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment)?.awaitMap() ?: return
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    map.uiSettings.isMyLocationButtonEnabled = false
    checkIfPermissionsGranted()
  }

  private fun checkIfPermissionsGranted() {

    if (ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      doAfterLocationGranted()
    } else {

      when {
        mapsModel.isFirstTime -> requestPermissions(
          arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
          LOCATION_REQUEST_CODE
        )
        shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> showReminderPermission()
        else -> showSettingsShortcut()
      }
    }
  }

  private fun showSettingsShortcut() {

    Snackbar.make(
      bind.root,
      "We need location permissions to show you near Gymkhanas",
      Snackbar.LENGTH_INDEFINITE
    )
      .setAction("Settings") {
        Log.d(classTag, "Moviendose a settings") //TODO
        try {
          startActivityForResult(
            Intent(
              Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
              Uri.fromParts("package", activity?.packageName, null)
            ), PERMISSION_SETTINGS_REQ_CODE
          )
        } catch (e: Exception) {
          Log.d(classTag, "Error al intentar ir a Settings")
        }
      }.show()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == PERMISSION_SETTINGS_REQ_CODE) checkIfPermissionsGranted()
  }

  private fun showReminderPermission() {

    Snackbar.make(
      bind.root,
      "We need location permission to show you near Gymkhanas",
      Snackbar.LENGTH_INDEFINITE
    )
      .setAction("Enable") {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
      }.show()
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {

    if (requestCode == LOCATION_REQUEST_CODE && grantResults.isNotEmpty()) {
      when {
        grantResults[0] == PackageManager.PERMISSION_GRANTED -> doAfterLocationGranted()
        shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> showReminderPermission()
        else -> showSettingsShortcut()
      }
      mapsModel.isFirstTime = false
    }
  }

  private fun doAfterLocationGranted() {

    locationLayer(enable = true)
    bind.locationButton.setOnClickListener { mapsModel.switchFollowing() }

    mapsModel.followingStatus.observe(viewLifecycleOwner, Observer {
      when (it) {
        MapsFragmentModel.FollowingStatus.FOLLOWING -> {
          fusedLocationClient.requestLocationUpdates(
            mapsModel.locationRequest, mapsModel.locationCallback,
            Looper.getMainLooper()
          )
          bind.locationButton.imageTintList = enabledColor
        }
        MapsFragmentModel.FollowingStatus.DISABLED -> {
          fusedLocationClient.removeLocationUpdates(mapsModel.locationCallback)
          bind.locationButton.imageTintList = disabledColor
        }
        else -> {
        }
      }
    })

    mapsModel.currentLoc.observe(viewLifecycleOwner, Observer {
      if (it != null) {
        Log.d(classTag, "Actualizando la loc -> ${it.toLatLng()}")
        if (isFirstLoc) {
          isFirstLoc = false
          it.zoomCamera(animate = false)
        } else it.zoomCamera()
      }
    })
    checkSettings()
  }

  private fun locationLayer(enable: Boolean) {

    map.isMyLocationEnabled = enable
    if (enable) {
      bind.locationButton.visibility = View.VISIBLE //Antes hide/show
      mapsModel.startFollowing()
    } else {
      bind.locationButton.visibility = View.GONE
      mapsModel.stopFollowing()
    }
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

      Log.d(classTag, "check settings SUCCESSFUL") //TODO borrar
    }

    task.addOnFailureListener {
      Log.d(classTag, "check settings FAILED")
      locationLayer(enable = false)
      Snackbar.make(
        bind.root,
        "Enable location settings to see near Gymkhanas",
        Snackbar.LENGTH_INDEFINITE
      )
        .setAction("Enable") {
          Log.d(classTag, "Activando opciones") //TODO

          locationLayer(enable = true)
        }.show()
    }
  }

  private fun Location.zoomCamera(animate: Boolean = true) {

    val cameraUpdate =
      CameraUpdateFactory.newLatLngBounds(toLatLng().createBounds(DEFAULT_VIEW_RADIUS), 0)
    if (animate) map.animateCamera(cameraUpdate)
    else map.moveCamera(cameraUpdate)
  }

  override fun onPause() {
    super.onPause()
    mapsModel.stopFollowing()
  }

  override fun onResume() {
    super.onResume()
    mapsModel.startFollowing()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _bind = null
  }
}