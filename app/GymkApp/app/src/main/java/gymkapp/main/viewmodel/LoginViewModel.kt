package gymkapp.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gymkapp.main.USER_MAX_LENGTH
import gymkapp.main.USER_MIN_LENGTH
import gymkapp.main.api.RemoteAPI
import gymkapp.main.model.User

/**
 * Clase para gestionar el estado de sesión del usuario
 */
class LoginViewModel : ViewModel() {

  enum class AuthenticationState {
    UNAUTHENTICATED,
    AUTHENTICATED,
    INVALID_AUTHENTICATION
  }

  //Propiedad observable que representa el estado actual de la sesion
  val authenticationState = MutableLiveData(AuthenticationState.INVALID_AUTHENTICATION)

  var user: User? = null
    private set
  var errorMessage = ""
    private set

  fun authenticate(token: String?) {

    authenticationState.value = token?.let { User(token); AuthenticationState.AUTHENTICATED }
      ?: AuthenticationState.UNAUTHENTICATED
  }

  suspend fun login(user: String, password: String) {

    val (failure, message) = RemoteAPI.login(user, password)
    if (failure) loginFailed(message)
    else {
      this.user = message
      authenticationState.value =
        AuthenticationState.AUTHENTICATED
    }
  }

  private fun loginFailed(message: String) {
    errorMessage = message
    authenticationState.value =
      AuthenticationState.INVALID_AUTHENTICATION
    authenticationState.value =
      AuthenticationState.UNAUTHENTICATED
  }

  fun logout() {

    user = null
    authenticationState.value =
      AuthenticationState.UNAUTHENTICATED
  }

  companion object {

    fun validateUsername(username: String) = with(username){
      when{
        isEmpty() -> "Username must not be empty"
        length !in USER_MIN_LENGTH..USER_MAX_LENGTH -> "3 to 20 characters required"
        contains(' ') -> "No spaces allowed"
        else -> null
      }
    }

    fun validatePassword(password: String) = with(password){

      when{
        isEmpty() -> "Password must not be empty"
        length < 8 -> "At least 8 characters are required"
        else -> {
          var space = false
          var uppercase = false
          forEach {
            if(it.isUpperCase())uppercase = true
            else if(it==' ')space = true
          }
          if(space) "No spaces allowed"
          else if(!uppercase) "At least one uppercase is required"
          else null
        }
      }
    }
  }
}