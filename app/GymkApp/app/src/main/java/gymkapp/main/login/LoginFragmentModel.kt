package gymkapp.main.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gymkapp.main.LoginViewModel

/**
 * Clase para manejar los datos de la pantalla o vista de LOGIN
 */
class LoginFragmentModel : ViewModel() {

  val isDataValid = MutableLiveData(false)
  private var correctUsername = false
    set(value) {
      field = value
      isDataValid.value = field && correctPassword
    }
  private var correctPassword = false
    set(value) {
      field = value
      isDataValid.value = field && correctUsername
    }

  fun validateUsername(username: String) =
    LoginViewModel.validateUsername(username).also { correctUsername = it == null }

  fun validatePassword(password: String) =
    LoginViewModel.validatePassword(password).also { correctPassword = it == null }
}