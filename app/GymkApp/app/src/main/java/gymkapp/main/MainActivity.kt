package gymkapp.main

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import gymkapp.main.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  fun onClickButtonToMap(view: View){
    startActivity(Intent(this,MapsActivity::class.java))
  }

  fun onClickButtonToLogin(view: View){
    startActivity(Intent(this,LoginActivity::class.java))
  }
}
