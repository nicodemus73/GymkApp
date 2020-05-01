package gymkapp.main.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import gymkapp.main.FTUELoginDirections
import gymkapp.main.R
import gymkapp.main.databinding.RegisterBinding
import gymkapp.main.viewmodel.LoginViewModel
import gymkapp.main.viewmodel.login.RegisterFragmentModel
import gymkapp.main.viewmodel.login.RegisterViewModel
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

  private val registrationViewModel: RegisterViewModel by viewModels()
  private val registerFragmentModel: RegisterFragmentModel by viewModels()
  private val loginViewModel: LoginViewModel by activityViewModels()
  private lateinit var errorSnackbar: Snackbar
  private var _bind: RegisterBinding? = null
  private val bind: RegisterBinding inline get() = _bind!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _bind = RegisterBinding.inflate(inflater, container, false)
    return bind.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    val navController = findNavController()
    val fieldUsername = bind.edTextUser
    val fieldPassword = bind.edTextPass
    val fieldCPassword = bind.edTextCPass

    //OnClickListeners - Botones
    bind.buttonBack.setOnClickListener { navController.navigateUp() }
    bind.registerButton.setOnClickListener {
      viewLifecycleOwner.lifecycleScope.launch {
        registrationViewModel.register(fieldUsername.text.toString(), fieldPassword.text.toString())
      }
    }

    //Activacion del boton cuando los datos sean validos
    registerFragmentModel.isDataValid.observe(viewLifecycleOwner, Observer { valid ->
      bind.registerButton.isEnabled = valid
    })

    //Comprobacion de los datos y mostrar el mensaje de error si no lo son
    fieldUsername.doAfterTextChanged {
      bind.inputUsername.error =
        registerFragmentModel.validateUsername(fieldUsername.text.toString())
    }
    fieldPassword.doAfterTextChanged {
      val pass = fieldPassword.text.toString()
      bind.inputPassword.error = registerFragmentModel.validatePassword(pass)
      bind.inputConfirmPassword.error =
        registerFragmentModel.checkEquals(pass, fieldCPassword.text.toString())
    }
    fieldCPassword.doAfterTextChanged {
      bind.inputConfirmPassword.error = registerFragmentModel.checkEquals(
        fieldPassword.text.toString(),
        fieldCPassword.text.toString()
      )
    }

    //Manejo del estado de autenticacion (LOGIN)
    loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authState ->
      when (authState) {

        LoginViewModel.AuthenticationState.AUTHENTICATED -> {

          Log.d(javaClass.simpleName, "Autenticado, guardando y moviendose al grafico principal")
          activity?.getPreferences(Context.MODE_PRIVATE)
            ?.edit { putString(R.string.TokenKey.toString(), loginViewModel.user!!.id) }
          navController.navigate(FTUELoginDirections.toMainGraph())
        }
        //Improbable que ocurra ya que el registro deberia haber ido bien
        LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION -> {
          Log.d(javaClass.simpleName, "Logeo no ha salido bien")
          errorSnackbar = Snackbar.make(view, loginViewModel.errorMessage, Snackbar.LENGTH_SHORT)
            .setAction("Ignore") {}
          errorSnackbar.show()
        }
        else -> {
        }
      }
    })

    //Manejo del estado de registro (REGISTER)
    registrationViewModel.registrationState.observe(viewLifecycleOwner, Observer { state ->
      when (state) {

        RegisterViewModel.RegistrationState.REGISTRATION_COMPLETED -> {
          Log.d(javaClass.simpleName, "Registro completado, logeandose")
          viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.login(fieldUsername.text.toString(), fieldPassword.text.toString())
          }
        }
        RegisterViewModel.RegistrationState.REGISTRATION_FAILED -> {
          Log.d(javaClass.simpleName, "Registro incorrecto")
          errorSnackbar =
            Snackbar.make(view, registrationViewModel.errorMessage, Snackbar.LENGTH_SHORT)
              .setAction("Ignore") {}
          errorSnackbar.show()
        }
        else -> {
        }
      }
    })
  }

  override fun onStop() {
    super.onStop()
    if (::errorSnackbar.isInitialized) errorSnackbar.dismiss()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _bind = null
  }
}
