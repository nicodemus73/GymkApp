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
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.register.view.*

class RegisterFragment : Fragment() {

  private val registrationViewModel: RegisterViewModel by viewModels()
  private val registerFragmentModel: RegisterFragmentModel by viewModels()
  private val loginViewModel: LoginViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.register,container,false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    val navController = findNavController()
    view.buttonBack.setOnClickListener { navController.navigateUp() } //Nota: no permitir esta llamada cuando la pila solo tiene el startDestination, o gestionarla con finish()
    view.registerButton.setOnClickListener {
      registrationViewModel.register(
        view.inputUsername.editText?.text.toString(),
        view.inputPassword.editText?.text.toString()
      )
    }

    registerFragmentModel.isDataValid.observe(viewLifecycleOwner, Observer { valid ->
      view.registerButton.isEnabled = valid
    })

    view.inputUsername.editText?.doAfterTextChanged {
      view.inputUsername.error = registerFragmentModel.validateUsername(view.inputUsername.editText?.text.toString())
      registerFragmentModel.checkIsDataValid()
    }
    view.inputPassword.editText?.doAfterTextChanged {
      val pass = view.inputPassword.editText?.text.toString()
      view.inputPassword.error = registerFragmentModel.validatePassword(pass)
      view.inputConfirmPassword.error = registerFragmentModel.checkEquals(pass,view.inputConfirmPassword.editText?.text.toString())
      registerFragmentModel.checkIsDataValid()
    }
    view.inputConfirmPassword.editText?.doAfterTextChanged {
      view.inputConfirmPassword.error = registerFragmentModel.checkEquals(view.inputConfirmPassword.editText?.text.toString(),view.inputPassword.editText?.text.toString())
      registerFragmentModel.checkIsDataValid()
    }

    loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authState ->
      when(authState){
        LoginViewModel.AuthenticationState.AUTHENTICATED -> {

          activity?.getPreferences(Context.MODE_PRIVATE)?.edit { putString(R.string.TokenKey.toString(),loginViewModel.loginToken) }
          navController.navigate(FTUELoginDirections.toMainGraph())
        }
        LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION -> {
          val message = loginViewModel.errorMessage
          Snackbar.make(view,message,1).show()
        }
        else -> {}
      }
    })

    registrationViewModel.registrationState.observe(viewLifecycleOwner, Observer { state ->
      when(state){
        RegisterViewModel.RegistrationState.REGISTRATION_COMPLETED -> loginViewModel.login(view.inputUsername.editText?.text.toString(),view.inputPassword.editText?.text.toString())
        RegisterViewModel.RegistrationState.REGISTRATION_FAILED -> {
          val message = registrationViewModel.errorMessage
          Snackbar.make(view,message,1).show()
        }
        else -> {}
      }
    })
  }
}
