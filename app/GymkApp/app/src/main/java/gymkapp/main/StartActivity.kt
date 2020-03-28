package gymkapp.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_start)
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE //TODO probar desactivar flags del navbar para evitar perder contenido importante en la vista de mapas
      .or(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
      .or(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
      .or(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
      .or(View.SYSTEM_UI_FLAG_FULLSCREEN)
      .or(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
  }

  fun onClickLoginButton(view: View){
    //TODO Saltar a la vista de login
  }

  fun onClickRegisterLink(view: View){
    //TODO Saltar a la vista de registro
  }
}
