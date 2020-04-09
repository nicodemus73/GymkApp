package gymkapp.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Clase para gestionar el estado de sesión del usuario
 */
class LoginViewModel: ViewModel() {

  enum class AuthenticationState {
    UNAUTHENTICATED,
    AUTHENTICATED,
    INVALID_AUTHENTICATION
  }

  //Propiedad observable que representa el estado actual de la sesion
  val authenticationState = MutableLiveData(AuthenticationState.UNAUTHENTICATED)
  var loginToken: String? = null
    private set
  var errorMessage = ""
    private set

  /**
   * Al entrar el valor de authenticationState debería ser "UNAUTHENTICATED"
   */
  suspend fun login(user:String, password:String){

    val (failure,message) = RemoteAPI.login(user,password)
    if(failure) loginFailed(message)
    else {
      loginToken = message
      authenticationState.value = AuthenticationState.AUTHENTICATED
    }
  }

  private fun loginFailed(message:String){
    errorMessage = message
    authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
    authenticationState.value = AuthenticationState.UNAUTHENTICATED
  }
}