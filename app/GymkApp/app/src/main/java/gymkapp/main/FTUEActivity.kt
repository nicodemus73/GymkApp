package gymkapp.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import gymkapp.main.LoginViewModel.AuthenticationState.*

class FTUEActivity : AppCompatActivity() {

  val loginViewModel: LoginViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.login_host)

    val navController = findNavController(R.id.login_content)

    loginViewModel.authenticationState.observe(this, Observer { authState ->
      if (authState == AUTHENTICATED) {
        Log.d(javaClass.name, "Autenticado, moviendose a la actividad principal")
        startActivity(Intent(this,MainActivity::class.java).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
          putExtra("gymkapp.main.extra.${R.string.TokenKey}",loginViewModel.loginToken)
        })
      }
    })
  }
}
