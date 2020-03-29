package gymkapp.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import gymkapp.main.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    //TODO Resposiveness!!!
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_start)
    /*
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE //TODO probar desactivar flags del navbar para evitar perder contenido importante en la vista de mapas
      .or(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
      .or(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
      .or(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
      .or(View.SYSTEM_UI_FLAG_FULLSCREEN)
      .or(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
      */
  }

  fun onClickLoginButton(view: View){
    startActivity(Intent(this,LoginActivity::class.java))
  }

  fun onClickRegisterLink(view: View){
    startActivity(Intent(this,RegisterActivity::class.java))
  }
}
