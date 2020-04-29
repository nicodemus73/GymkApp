package gymkapp.main.ui.login

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import gymkapp.main.FTUELoginDirections
import gymkapp.main.R

class WelcomeFragmentDirections private constructor() {
  companion object {
    fun toRegister(): NavDirections = ActionOnlyNavDirections(R.id.toRegister)

    fun toLogin(): NavDirections = ActionOnlyNavDirections(R.id.toLogin)

    fun toMainGraph(): NavDirections = FTUELoginDirections.toMainGraph()

    fun toLoginFTUE(): NavDirections = FTUELoginDirections.toLoginFTUE()
  }
}
