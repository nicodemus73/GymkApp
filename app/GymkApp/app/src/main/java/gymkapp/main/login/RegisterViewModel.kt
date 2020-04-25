package gymkapp.main.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gymkapp.main.api.RemoteAPI

class RegisterViewModel : ViewModel() {

  enum class RegistrationState {
    COLLECTING_DATA,
    REGISTRATION_COMPLETED,
    REGISTRATION_FAILED
  }
  //TODO: Cifrar usernae+password

  /**
   * Al registrationState se le tiene que colocar un observer que cuando este COMPLETED salte a la pantalla principal
   * y cuando falle ponga en pantalla que ha fallado mediante una alerta
   */
  val registrationState = MutableLiveData(RegistrationState.COLLECTING_DATA)
  var errorMessage = ""
    private set

  /**
   * Al llegar aqui los datos de registro deberían ser validos
   * en formato (el boton no se activa hasta que no son validos)
   */
  suspend fun register(user: String, password: String) {

    val (failure, message) = RemoteAPI.register(
      user,
      password
    )
    if (failure) registrationFailed(message)
    else registrationState.value =
      RegistrationState.REGISTRATION_COMPLETED
  }

  private fun registrationFailed(message: String) {
    errorMessage = message
    registrationState.value =
      RegistrationState.REGISTRATION_FAILED
    registrationState.value =
      RegistrationState.COLLECTING_DATA
  }
}