package gymkapp.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel: ViewModel() {

  enum class RegistrationState{
    COLLECTING_DATA,
    REGISTRATION_COMPLETED,
    REGISTRATION_FAILED
  }

  val registrationState = MutableLiveData<RegistrationState>(RegistrationState.COLLECTING_DATA)
  var loginToken = ""
    private set

  /**
   * Al llegar aqui los datos de registro deberían ser validos
   * en formato (el boton no se activa hasta que no son validos)
   */
  fun register(user: String, password: String, confPass:String){

  }
}