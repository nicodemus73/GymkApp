package gymkapp.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {

  enum class AuthenticationState {
    UNAUTHENTICATED,
    AUTHENTICATED,
    INVALID_AUTHENTICATION
  }

  val authenticationState = MutableLiveData<AuthenticationState>()

  init {
    authenticationState.value = AuthenticationState.UNAUTHENTICATED //Comprobar si el existe un "LoginToken" mediante el sharedpreferences
  }

}