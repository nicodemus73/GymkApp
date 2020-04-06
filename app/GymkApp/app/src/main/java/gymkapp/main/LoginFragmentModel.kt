package gymkapp.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Clase para manejar los datos de la pantalla o vista de LOGIN
 */
class LoginFragmentModel: ViewModel() {

  val isDataValid = MutableLiveData(false)
  private var correctUsername = false
    set(value){
      field = value
      isDataValid.value = field && correctPassword
    }
  private var correctPassword = false
    set(value){
      field = value
      isDataValid.value = field && correctUsername
    }

  fun validateUsername(username:String) = when {

    username.isEmpty() -> "Username must not be empty"
    username.contains(" ") -> "No spaces permitted"
    username.length !in 3..20 -> "Length must be 3 to 20 characters"
    else -> null
  }.also { correctUsername = it==null }

  fun validatePassword(password: String) = when {

    password.isEmpty() -> "Password must not be empty"
    password.length < 8 -> "At least 8 characters are required"
    password.contains(" ") -> "No spaces permitted"
    password.firstOrNull { it.isUpperCase() } == null -> "At least one uppercase is required"
    else -> null
  }.also { correctPassword = it==null }

  //A borrar, ejemplo para ver como loggear errores, visible desde Logcat, aunque se cierre la app
  override fun onCleared() {
    Log.d(LoginFragmentModel::class.java.simpleName,"Me estan borrando D:")
  }
}