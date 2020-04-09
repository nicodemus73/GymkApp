package gymkapp.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.settings.*
import gymkapp.main.MainViewModel.SesionState.*
import kotlinx.android.synthetic.main.settings.view.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

  val sesionModel: MainViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.settings, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    val navController = findNavController()

    buttonLogout.setOnClickListener {
      activity?.getPreferences(Context.MODE_PRIVATE)?.edit {
        remove(R.string.TokenKey.toString())
      }?.also {
        sesionModel.logout()
      } ?: Log.d(javaClass.name,"Error al intentar cerrar la sesion")
    }

    view.nightModeSwitch.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
      else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
  }
}
