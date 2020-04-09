package gymkapp.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.main_host.*
import java.lang.Exception
import gymkapp.main.MainViewModel.SesionState.*

const val EXTRA_PREFIX = "gymkapp.main.extra."

class MainActivity : AppCompatActivity() {

  private val sesionModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    //TODO Resposiveness!!!
    //TODO cambiar nombres de botones,etc para no confundir al editor (IMPORTANTE)
    //TODO borrar fragmento de mapa, cuidado con los accesos a views que pueden ser nulos...
    //TODO Valor por defecto de Authenticate -> INVALID AUTHENTICATION
    // Me veo incapaz de mantener la coherencia con una sola actividad. Se rompe la barra de navegacion constantemente, haga lo que haga.

    super.onCreate(savedInstanceState)

    sesionModel.currentSesionState.observe(this, Observer {state ->
      when(state){
       UNKNOWN -> {

         intent.extras?.getString("$EXTRA_PREFIX${R.string.TokenKey}")?.run{

           val token = this
           Log.d(javaClass.name,"Recibo el token y lo guardo")
           sesionModel.checkActiveSesion(token)
           getPreferences(MODE_PRIVATE).edit {
             putString(R.string.TokenKey.toString(),token)
           }
         } ?: run{

           Log.d(javaClass.name,"Leo de disco el token")

           sesionModel.checkActiveSesion(try {
             getPreferences(MODE_PRIVATE).getString(R.string.TokenKey.toString(),null)
           } catch (e: Exception) {
             Log.d(javaClass.name,"Error al intentar leer el token de disco")
             null
           })
         }
       }
        INVALID -> {
          Log.d(javaClass.name,"No hay token, voy al login")
          startActivity(Intent(this,FTUEActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
        else -> {

          setContentView(R.layout.main_host)
          val navController = findNavController(R.id.main_content)
          bottomNavigationView.setupWithNavController(navController)
        }
      }
    })
  }
}
