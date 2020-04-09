package gymkapp.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

  enum class SesionState{
    UNKNOWN,
    ACTIVE,
    INVALID
  }
  val currentSesionState = MutableLiveData(SesionState.UNKNOWN)
  private var loginToken : String? = null

  /**
   * Funcion que permite que el usuario se autentique al iniciar la aplicacion
   */
  fun checkActiveSesion(token: String?){

    currentSesionState.value = token?.let { loginToken=it; SesionState.ACTIVE } ?: SesionState.INVALID
  }

  fun logout(){

    loginToken = null
    currentSesionState.value = SesionState.INVALID
  }
}