package gymkapp.main

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import gymkapp.main.LoginViewModel.AuthenticationState.*
import gymkapp.main.databinding.BottomNavBinding

class MainActivity : AppCompatActivity() {

  private val loginViewModel: LoginViewModel by viewModels()
  private lateinit var bind: BottomNavBinding

  @SuppressLint("SourceLockedOrientationActivity")
  override fun onCreate(savedInstanceState: Bundle?) {
    //TODO Resposiveness!!!
    //TODO SharedPreference cifrado
    //TODO secure.properties API KEY y otras formas de proteger la API KEY
    //TODO añadir loader para la conexion al server
    //TODO esconder el teclado al pasar a la vista de mapas -- Solo pasa con el teclado fisico... Ignorar por el momento
    super.onCreate(savedInstanceState)
    bind = BottomNavBinding.inflate(layoutInflater)
    requestedOrientation =
      ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //Desactivamos el modo "landscape"
    setContentView(bind.root)

    Log.d(javaClass.simpleName, "Me han creado")
    val navController = findNavController(R.id.fragments_content)
    bind.bottomNavigationView.setupWithNavController(navController)
    loginViewModel.authenticationState.observe(
      this,
      Observer {
        bind.bottomNavigationView.visibility = if (it == AUTHENTICATED) View.VISIBLE else View.GONE
      })


    }
}
