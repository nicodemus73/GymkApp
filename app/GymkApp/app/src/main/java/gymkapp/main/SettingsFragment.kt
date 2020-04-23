package gymkapp.main

import android.content.Context
import android.os.Bundle
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

    val navController = findNavController()
    val context = preferenceManager.context
    val screen = preferenceManager.createPreferenceScreen(context)

    val category = PreferenceCategory(context).apply {
      title = "Settings"
    }//TODO añadir directamente
    val nightModePref = SwitchPreferenceCompat(context).apply {
      key = "nightModeSetting"
      title = "Night Mode"
    }
    val changePasswordPref = Preference(context).apply {
      key = "changePasswordSetting"
      title = "Change Password"
    }

    val logOutPref = Preference(context).apply {
      key = "logOutSetting"
      title = "Log Out"
      isVisible = true //TODO probar
    }

    /*
    preferenceLogOut?.setOnPreferenceClickListener {
      activity?.getPreferences(Context.MODE_PRIVATE)?.edit {
        remove(R.string.TokenKey.toString())
      }
      navController.navigate(SettingsFragmentDirections.toLoginFTUE())
      loginModel.logout()
      true
    }

    preferenceNightMode?.setOnPreferenceClickListener {

      true
    }*/

    with(screen){
      addPreference(category)
      addPreference(nightModePref)
      addPreference(changePasswordPref)
      addPreference(logOutPref)
    }
  }
}
