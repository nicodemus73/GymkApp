package gymkapp.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.login.view.*
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

  /**
   * Esta y la de registro sirven para observar los cambios de validez en los inputFields y activar o desactivar el boton de login (o registro)
   * Scope de fragmento!
   */
  private val loginFragmentModel: LoginFragmentModel by viewModels()
  /**
   * Instancia que sirve para mantener datos independientemente del estado del fragmento. ViewModel
   * provee funciones para gestionar el estado del login, el estado del registro, para cambiar estados
   * y guardar datos sobre el usuario a lo largo de la aplicacion como el nombre de usuario o el authenticationToken
   */
  private val viewModel: LoginViewModel by activityViewModels()
  private lateinit var errorSnackBar: Snackbar

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.login,container,false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    val navController = findNavController()

    //OnClickListeners - Botones
    view.buttonBack2.setOnClickListener { navController.navigateUp() }
    view.loginButtonLF.setOnClickListener {
      viewLifecycleOwner.lifecycleScope.launch {
        viewModel.login(view.inputUsername.editText?.text.toString(),view.inputPassword.editText?.text.toString())
      }
    }

    //Activacion del boton de login
    loginFragmentModel.isDataValid.observe(viewLifecycleOwner, Observer { valid ->
      view.loginButtonLF.isEnabled = valid
    })

    //Si el texto cambia se tiene que validar y mostrar error si sale mal
    view.inputUsername.editText?.doAfterTextChanged {
      view.inputUsername.error = loginFragmentModel.validateUsername(view.inputUsername.editText?.text.toString())
    }
    view.inputPassword.editText?.doAfterTextChanged {
      view.inputPassword.error = loginFragmentModel.validatePassword(view.inputPassword.editText?.text.toString())
    }

    //Seguimiento del estado de autenticacion (LOGIN)
    viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authState ->
      when(authState){
        LoginViewModel.AuthenticationState.AUTHENTICATED -> {

          activity?.getPreferences(Context.MODE_PRIVATE)?.edit { putString(R.string.TokenKey.toString(),viewModel.loginToken) }
          navController.navigate(FTUELoginDirections.toMainGraph())
        }
        LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION -> {
          errorSnackBar = Snackbar.make(view,viewModel.errorMessage,Snackbar.LENGTH_SHORT).setAction("Ignore"){}
          errorSnackBar.show()
        }
        else -> {}
      }
    })
  }

  override fun onStop() {
    super.onStop()
    if(::errorSnackBar.isInitialized) errorSnackBar.dismiss()
  }
}
