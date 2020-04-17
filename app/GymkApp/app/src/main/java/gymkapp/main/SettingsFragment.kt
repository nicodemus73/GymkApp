package gymkapp.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import gymkapp.main.databinding.SettingsBinding


class SettingsFragment : Fragment() {

  private val loginModel: LoginViewModel by activityViewModels()
  private var _bind : SettingsBinding? = null
  private val bind : SettingsBinding get() = _bind!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    _bind = SettingsBinding.inflate(inflater,container,false)
    return bind.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val navController = findNavController()
    bind.nightModeSwitch.setOnCheckedChangeListener{
        _,isChecked ->
      if(isChecked) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
      else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    bind.logoutButton.setOnClickListener {

      activity?.getPreferences(Context.MODE_PRIVATE)?.edit {
        remove(R.string.TokenKey.toString())
      }
      navController.navigate(SettingsFragmentDirections.toLoginFTUE())
      loginModel.logout()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _bind = null
  }
}
