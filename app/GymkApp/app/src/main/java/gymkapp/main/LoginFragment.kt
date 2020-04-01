package gymkapp.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

  /**
   * Instancia que sirve para mantener datos independientemente del estado del fragmento. ViewModel
   * provee funciones para gestionar el estado del login, el estado del registro, para cambiar estados
   * y guardar datos sobre el usuario a lo largo de la aplicacion como el nombre de usuario o el authenticationToken
   * Esta instanciación solo mantiene los datos en el "scope" del fragmento.
   * Para mantener la instancia durante la actividad (en una arquitectura de una sola actividad esto
   * seria muy util) mejor utilizar activityViewModel()
   */
  val model: LoginViewModel by viewModels()
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.login,container,false)

}
