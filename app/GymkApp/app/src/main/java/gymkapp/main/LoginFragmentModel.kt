package gymkapp.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginFragmentModel: ViewModel() {

  val isDataValid = MutableLiveData(false)
  private val fields = booleanArrayOf(false,false,false)

  fun validateUsername(username:String) = when {
      username.isEmpty() -> "Username must not be empty"
      username.contains(" ") -> "No spaces permitted"
      else -> null
    }.also { fields[0]= it==null }

  fun validatePassword(password: String) = when {

    password.isEmpty() -> "Password must not be empty"
    password.length < 8 -> "At least 8 characters are required"
    password.contains(" ") -> "No spaces permitted"
    password.firstOrNull { it.isUpperCase() } == null -> "At least one uppercase is required"
    else -> null
  }.also { fields[1] = it==null }

  fun checkEquals(pass:String,cpass:String) = (if(pass!=cpass) "Passwords are not equal" else null).also { fields[2]= it==null }
  fun checkIsDataValid() {
    isDataValid.value =  fields.all { it }
  }
}