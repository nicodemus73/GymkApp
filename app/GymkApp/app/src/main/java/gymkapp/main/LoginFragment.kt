package gymkapp.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.login.*

class LoginFragment : Fragment() {

  /**
   * Instancia que sirve para mantener datos independientemente del estado del fragmento. ViewModel
   * provee funciones para gestionar el estado del login, el estado del registro, para cambiar estados
   * y guardar datos sobre el usuario a lo largo de la aplicacion como el nombre de usuario o el authenticationToken
   */
  private val viewModel: LoginViewModel by activityViewModels()
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.login,container,false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    val navController = findNavController()
    buttonBack2.setOnClickListener { navController.navigateUp() }
  }
}
