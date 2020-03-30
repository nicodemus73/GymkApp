package gymkapp.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import gymkapp.main.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    //TODO Resposiveness!!!
    //TODO Quitar drawables, xmls, fuentes y vistas no utilizadas...
    //TODO Se puede guardar las preferencias de forma permanente mediante SharedPreferences (entre actividades?)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_start)
    /*
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE //TODO A borrar en el futuro...
      .or(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
      .or(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
      .or(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
      .or(View.SYSTEM_UI_FLAG_FULLSCREEN)
      .or(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
      */
    nightModeSwitch.setOnCheckedChangeListener{
        _,isChecked ->
      if(isChecked) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
      else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    loginButton.setOnClickListener{ startActivity(Intent(this,LoginActivity::class.java)) }
    registerButton.setOnClickListener { startActivity(Intent(this,RegisterActivity::class.java)) }
  }
}
