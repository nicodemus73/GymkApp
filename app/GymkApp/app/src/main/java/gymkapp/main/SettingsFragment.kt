package gymkapp.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat

class SettingsFragment : PreferenceFragmentCompat() {

  private val loginModel: LoginViewModel by activityViewModels()

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

    val classTag = javaClass.simpleName

    //val navController = findNavController()
    val context = preferenceManager.context
    val screen = preferenceManager.createPreferenceScreen(context)

    val category = PreferenceCategory(context).apply {
      title = "Settings"
    }

    //TODO estilos switches
    //TODO añadir iconos
    val followSystemNightMode = SwitchPreferenceCompat(context).apply {
      key = "followSystemNightMode"
      title = "Apply System Theme"
    }

    val nightModePref = SwitchPreferenceCompat(context).apply {
      key = "nightModeSetting"
      title = "Enable Dark Theme"
    }


    followSystemNightMode.setOnPreferenceChangeListener { _, isChecked ->
      when (isChecked) {
        is Boolean -> when {
          isChecked -> {
            Log.d(classTag,"SYSTEM MODE, chequeandose")
            setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
            nightModePref.isEnabled = false
          }
          else -> {
            Log.d(classTag,"SYSTEM MODE, deschequeandose")
            nightModePref.isEnabled = true
            setDefaultNightMode(if(nightModePref.isChecked) MODE_NIGHT_YES else MODE_NIGHT_NO)
          }
        }
      }
      true
    }

    nightModePref.setOnPreferenceChangeListener { _, _ ->

      Log.d(classTag, "Forzando ${if (nightModePref.isChecked) "modo noche" else "modo dia"}")
      //if (nightModePref.isChecked) setDefaultNightMode(MODE_NIGHT_YES)
      //else setDefaultNightMode(MODE_NIGHT_NO)
      true
    }

    val changePasswordPref = Preference(context).apply {
      title = "Change Password"
    }

    val logOutPref = Preference(context).apply {
      title = "Log Out"
      isPersistent = false
      setOnPreferenceClickListener {
        requireActivity().getPreferences(Context.MODE_PRIVATE)?.edit {
          remove(R.string.TokenKey.toString())
        }
        //navController.navigate(SettingsFragmentDirections.toLoginFTUE())
        loginModel.logout()
        true
      }
    }

    with(screen) {
      addPreference(category)
      addPreference(followSystemNightMode)
      addPreference(nightModePref)
      addPreference(changePasswordPref)
      addPreference(logOutPref)
    }
    preferenceScreen = screen
  }
}
