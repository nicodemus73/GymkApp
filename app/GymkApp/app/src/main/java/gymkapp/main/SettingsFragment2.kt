package gymkapp.main

import android.content.Context
import android.os.Bundle
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import gymkapp.main.databinding.SettingsBinding

class SettingsFragment2 : PreferenceFragmentCompat() {

    private val loginModel: LoginViewModel by activityViewModels()
    private var _bind: SettingsBinding? = null
    private val bind: SettingsBinding inline get() = _bind!!

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        val navController = findNavController()
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val preferenceLogOut = findPreference("logOutSetting") as Preference?
        preferenceLogOut?.setOnPreferenceClickListener {
            activity?.getPreferences(Context.MODE_PRIVATE)?.edit {
                remove(R.string.TokenKey.toString())
            }
            navController.navigate(SettingsFragmentDirections.toLoginFTUE())
            loginModel.logout()
            true
        }

        val preferenceNightMode = findPreference("logOutSetting") as Preference?
        preferenceNightMode?.setOnPreferenceClickListener {

            true
        }
    }
}
