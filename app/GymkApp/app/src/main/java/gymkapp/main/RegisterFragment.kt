package gymkapp.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.register.view.*
import kotlinx.coroutines.launch
import gymkapp.main.LoginViewModel.AuthenticationState.*
import gymkapp.main.RegisterViewModel.RegistrationState.*

class RegisterFragment : Fragment() {

  private val registrationViewModel: RegisterViewModel by viewModels()
  private val registerFragmentModel: RegisterFragmentModel by viewModels()
  private val loginViewModel: LoginViewModel by activityViewModels()
  private lateinit var errorSnackbar: Snackbar

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.register, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    //TODO: Mejorar tiempo de visualizacion del mensaje de error, que al cambiar de pantalla se destruya y que se pueda descartar
    val navController = findNavController()

    //OnClickListeners - Botones
    view.buttonBack.setOnClickListener { navController.navigateUp() }
    view.registerButton.setOnClickListener {
      viewLifecycleOwner.lifecycleScope.launch {
        registrationViewModel.register(
          view.inputUsername.editText?.text.toString(),
          view.inputPassword.editText?.text.toString()
        )
      }
    }

    //Activacion del boton cuando los datos sean validos
    registerFragmentModel.isDataValid.observe(viewLifecycleOwner, Observer { valid ->
      view.registerButton.isEnabled = valid
    })

    //Comprobacion de los datos y mostrar el mensaje de error si no lo son
    view.inputUsername.editText?.doAfterTextChanged {
      view.inputUsername.error =
        registerFragmentModel.validateUsername(view.inputUsername.editText?.text.toString())
    }
    view.inputPassword.editText?.doAfterTextChanged {
      val pass = view.inputPassword.editText?.text.toString()
      view.inputPassword.error = registerFragmentModel.validatePassword(pass)
      view.inputConfirmPassword.error =
        registerFragmentModel.checkEquals(pass, view.inputConfirmPassword.editText?.text.toString())
    }
    view.inputConfirmPassword.editText?.doAfterTextChanged {
      view.inputConfirmPassword.error = registerFragmentModel.checkEquals(
        view.inputConfirmPassword.editText?.text.toString(),
        view.inputPassword.editText?.text.toString()
      )
    }

    //Manejo del estado de autenticacion (LOGIN)
    loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authState ->
      if (authState == INVALID_AUTHENTICATION) {
        Log.d(javaClass.name, "Error en la autenticacion")
        errorSnackbar = Snackbar.make(view, loginViewModel.errorMessage, Snackbar.LENGTH_SHORT)
          .setAction("Ignore") {}
        errorSnackbar.show()
      }
    })

    //Manejo del estado de registro (REGISTER)
    registrationViewModel.registrationState.observe(viewLifecycleOwner, Observer { state ->
      when (state) {

        REGISTRATION_COMPLETED -> {
          Log.d(javaClass.name, "Registro exitoso, voy a hacer login")
          viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.login(
              view.inputUsername.editText?.text.toString(),
              view.inputPassword.editText?.text.toString()
            )
          }
        }
        REGISTRATION_FAILED -> {
          Log.d(javaClass.name, "Error al registrarse. Se mostrara un mensaje")
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
}
