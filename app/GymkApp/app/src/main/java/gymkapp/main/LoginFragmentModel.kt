package gymkapp.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Clase para manejar los datos de la pantalla o vista de LOGIN
 */
class LoginFragmentModel: ViewModel() {

  val isDataValid = MutableLiveData(false)
  private val fields = booleanArrayOf(false,false)

  fun validateUsername(username:String) = when {

    username.isEmpty() -> "Username must not be empty"
    username.contains(" ") -> "No spaces permitted"
    username.length > 20 -> "Username is too long"
    else -> null
  }.also { fields[0]= it==null }

  fun validatePassword(password: String) = when {

    password.isEmpty() -> "Password must not be empty"
    password.length < 8 -> "At least 8 characters are required"
    password.contains(" ") -> "No spaces permitted"
    password.firstOrNull { it.isUpperCase() } == null -> "At least one uppercase is required"
    else -> null
  }.also { fields[1] = it==null }

  fun checkIsDataValid(){
    isDataValid.value = fields.all { it }
  }
}