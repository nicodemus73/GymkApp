package gymkapp.main.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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

  fun validateUsername(username: String) = when {
    username.isEmpty() -> "Username must not be empty"
    username.contains(" ") -> "No spaces permitted"
    username.length !in 3..20 -> "Length must be 3 to 20 characters"
    else -> null
  }.also { correctUsername = it == null }

  fun validatePassword(password: String) = when {

    password.isEmpty() -> "Password must not be empty"
    password.length < 8 -> "At least 8 characters are required"
    password.contains(" ") -> "No spaces permitted"
    password.none { it.isUpperCase() } -> "At least one uppercase is required"
    else -> null
  }.also { correctPassword = it == null }

  fun checkEquals(pass: String, cpass: String) =
    (if (pass != cpass) "Passwords are not equal" else null).also { correctConfPass = it == null }
}