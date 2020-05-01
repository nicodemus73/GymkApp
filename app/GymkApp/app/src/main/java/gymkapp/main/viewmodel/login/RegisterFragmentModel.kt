package gymkapp.main.viewmodel.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gymkapp.main.viewmodel.LoginViewModel

/**
 * Clase para manejar los datos de la vista de REGISTRO
 */
class RegisterFragmentModel : ViewModel() {

  val isDataValid = MutableLiveData(false)
  private val checkIsDataValid
    inline get() = correctUsername && correctPassword && correctConfPass
  private var correctUsername = false
    set(value) {
      field = value; isDataValid.value = checkIsDataValid
    }
  private var correctPassword = false
    set(value) {
      field = value; isDataValid.value = checkIsDataValid
    }
  private var correctConfPass = false
    set(value) {
      field = value; isDataValid.value = checkIsDataValid
    }

  fun validateUsername(username: String) = LoginViewModel.validateUsername(username).also { correctUsername = it == null }
  fun validatePassword(password: String) = LoginViewModel.validatePassword(password).also { correctPassword = it == null }
  fun checkEquals(pass: String, cpass: String) =
    (if (pass != cpass) "Passwords are not equal" else null).also { correctConfPass = it == null }
}