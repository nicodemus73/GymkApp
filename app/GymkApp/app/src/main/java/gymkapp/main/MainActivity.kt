package gymkapp.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    //TODO Resposiveness!!!
    //TODO Quitar drawables, xmls, fuentes y vistas no utilizadas...
    //TODO Se puede guardar las preferencias de forma permanente mediante SharedPreferences (entre actividades?)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_host)
    /*
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE //TODO A borrar en el futuro...
      .or(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
      .or(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
      .or(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
      .or(View.SYSTEM_UI_FLAG_FULLSCREEN)
      .or(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
      */
  }
}
