package gymkapp.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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
        view.inputPassword.editText?.text.toString(),
        view.inputConfirmPassword.editText?.text.toString()
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
  }
}
