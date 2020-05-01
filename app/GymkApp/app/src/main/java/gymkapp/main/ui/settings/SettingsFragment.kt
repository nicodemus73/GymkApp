package gymkapp.main.ui.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.*
import gymkapp.main.R
import gymkapp.main.viewmodel.LoginViewModel

class SettingsFragment : PreferenceFragmentCompat() {

  private val loginModel: LoginViewModel by activityViewModels()

  private fun nightMode(enable: Boolean?) {
    enable?.let { setDefaultNightMode(if (it) MODE_NIGHT_YES else MODE_NIGHT_NO) }
      ?: setDefaultNightMode(
        MODE_NIGHT_FOLLOW_SYSTEM
      )
  }

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

    val classTag = javaClass.simpleName
    Log.d(classTag, "Preerencias creadas")

    val context = preferenceManager.context
    val screen = preferenceManager.createPreferenceScreen(context)

    val category = PreferenceCategory(context).apply {
      title = "Settings"
    }

    //TODO estilos switches
    //TODO añadir iconos
    val followSystemNightMode = SwitchPreferenceCompat(context).apply {
      key = R.string.NightModeSysKey.toString()
      title = "Apply System Theme"
    }

    val nightModePref = SwitchPreferenceCompat(context).apply {
      key = R.string.NightModeKey.toString()
      title = "Enable Dark Theme"
      isEnabled = !PreferenceManager.getDefaultSharedPreferences(requireContext())
        .getBoolean(followSystemNightMode.key, false)
    }
    followSystemNightMode.setOnPreferenceChangeListener { _, checked ->
      with(nightModePref) {
        isEnabled = !(checked as Boolean)
        if (checked) nightMode(null)
        else nightMode(enable = isChecked)
      }
      true
    }

    nightModePref.setOnPreferenceChangeListener { _, isChecked ->
      nightMode(enable = isChecked as Boolean)
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
        findNavController().navigate(SettingsFragmentDirections.toLoginFTUE())
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
