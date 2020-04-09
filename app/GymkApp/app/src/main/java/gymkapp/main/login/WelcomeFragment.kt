package gymkapp.main.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import gymkapp.main.R
import gymkapp.main.login.WelcomeFragmentDirections
import kotlinx.android.synthetic.main.welcome.view.*

class WelcomeFragment : Fragment() {

  //TODO Valor por defecto de autenticacion -> UNAUTHENTICATED (o deberia)

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.welcome, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val navController = findNavController()
    view.loginButton.setOnClickListener { navController.navigate(WelcomeFragmentDirections.welcomeToLogin()) }
    view.registerButton.setOnClickListener { navController.navigate(WelcomeFragmentDirections.welcomeToRegister()) }
  }
}
