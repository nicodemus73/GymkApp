package gymkapp.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.bottom_nav.*
import gymkapp.main.LoginViewModel.AuthenticationState.*

class MainActivity : AppCompatActivity() {

  private val loginViewModel: LoginViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    //TODO Resposiveness!!!
    //TODO cambiar nombres de botones,etc para no confundir al editor (IMPORTANTE)
    //TODO borrar fragmento de mapa, cuidado con los accesos a views que pueden ser nulos...
    super.onCreate(savedInstanceState)
    setContentView(R.layout.bottom_nav)

    Log.d(javaClass.simpleName,"Me han creado")
    val navController = findNavController(R.id.fragments_content)
    bottomNavigationView.setupWithNavController(navController)
    loginViewModel.authenticationState.observe(this, Observer { bottomNavigationView.visibility = if(it==AUTHENTICATED) View.VISIBLE else View.GONE })
  }
}
