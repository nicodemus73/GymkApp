package gymkapp.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gymkapp.main.api.RemoteAPI

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
  val authenticationState = MutableLiveData(AuthenticationState.INVALID_AUTHENTICATION)
  //valor en memoria del loginToken (Podriamos querer observarlo? No creo...)
  var loginToken: String? = null
    private set
  var errorMessage = ""
    private set

  fun authenticate(token: String?){

    authenticationState.value = token?.let { loginToken=it; AuthenticationState.AUTHENTICATED } ?: AuthenticationState.UNAUTHENTICATED
  }

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

  fun logout(){

    loginToken = null
    authenticationState.value = AuthenticationState.UNAUTHENTICATED
  }
}