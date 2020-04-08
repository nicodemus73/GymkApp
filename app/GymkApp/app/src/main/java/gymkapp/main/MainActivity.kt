package gymkapp.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.bottom_nav.*

class MainActivity : AppCompatActivity() {

  private val loginViewModel: LoginViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    //TODO Resposiveness!!!
    //TODO cambiar nombres de botones,etc para no confundir al editor (IMPORTANTE)
    //TODO borrar fragmento de mapa, cuidado con los accesos a views que pueden ser nulos...
    super.onCreate(savedInstanceState)
    setContentView(R.layout.bottom_nav)

    val navController = findNavController(R.id.fragments_content)
    bottomNavigationView.setupWithNavController(navController)

    loginViewModel.authenticationState.observe(this, Observer {state ->
      if(state == LoginViewModel.AuthenticationState.AUTHENTICATED) bottomNavigationView?.visibility = View.VISIBLE
      else if(state == LoginViewModel.AuthenticationState.UNAUTHENTICATED) bottomNavigationView?.visibility = View.GONE
    })
  }
}
