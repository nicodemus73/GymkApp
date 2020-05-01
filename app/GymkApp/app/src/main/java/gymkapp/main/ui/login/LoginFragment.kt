package gymkapp.main.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import gymkapp.main.FTUELoginDirections
import gymkapp.main.viewmodel.LoginViewModel
import gymkapp.main.R
import gymkapp.main.databinding.LoginBinding
import gymkapp.main.viewmodel.login.LoginFragmentModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

  /**
   * Esta y la de registro sirven para observar los cambios de validez en los inputFields y activar o desactivar el boton de login (o registro)
   * Scope de fragmento!
   */
  private val loginFragmentModel: LoginFragmentModel by viewModels()
  private var _bind: LoginBinding? = null
  private val bind: LoginBinding inline get() = _bind!!

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
  ): View? {
    _bind = LoginBinding.inflate(inflater, container, false)
    return bind.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    val navController = findNavController()
    val fieldUsername = bind.edTextUserLogin
    val fieldPassword = bind.edTextPassLogin

    //OnClickListeners - Botones
    bind.buttonBack2.setOnClickListener { navController.navigateUp() }
    bind.loginButtonLF.setOnClickListener {
      viewLifecycleOwner.lifecycleScope.launch {
        viewModel.login(fieldUsername.text.toString(), fieldPassword.text.toString())
      }
    }

    //Activacion del boton de login
    loginFragmentModel.isDataValid.observe(viewLifecycleOwner, Observer { valid ->
      bind.loginButtonLF.isEnabled = valid
    })

    //Si el texto cambia se tiene que validar y mostrar error si sale mal
    fieldUsername.doAfterTextChanged {
      bind.inputUsername.error = loginFragmentModel.validateUsername(fieldUsername.text.toString())
    }
    fieldPassword.doAfterTextChanged {
      bind.inputPassword.error = loginFragmentModel.validatePassword(fieldPassword.text.toString())
    }

    //Seguimiento del estado de autenticacion (LOGIN)
    viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authState ->
      when (authState) {
        LoginViewModel.AuthenticationState.AUTHENTICATED -> {

          Log.d(javaClass.simpleName, "Autenticado, guardando y pasando al grafico principal")
          activity?.getPreferences(Context.MODE_PRIVATE)
            ?.edit { putString(R.string.TokenKey.toString(), viewModel.user!!.id) }
          navController.navigate(FTUELoginDirections.toMainGraph())
        }
        LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION -> {
          Log.d(javaClass.simpleName, "Entrada invalida, imprimiendo mensaje de error")
          errorSnackBar = Snackbar.make(view, viewModel.errorMessage, Snackbar.LENGTH_SHORT)
            .setAction("Ignore") {}
          errorSnackBar.show()
        }
        else -> {
        }
      }
    })
  }

  override fun onStop() {
    super.onStop()
    if (::errorSnackBar.isInitialized) errorSnackBar.dismiss()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _bind = null
  }
}
