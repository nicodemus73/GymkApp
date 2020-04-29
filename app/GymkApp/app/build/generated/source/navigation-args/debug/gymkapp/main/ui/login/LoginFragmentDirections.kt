package gymkapp.main.ui.login

import androidx.navigation.NavDirections
import gymkapp.main.FTUELoginDirections

class LoginFragmentDirections private constructor() {
  companion object {
    fun toMainGraph(): NavDirections = FTUELoginDirections.toMainGraph()

    fun toLoginFTUE(): NavDirections = FTUELoginDirections.toLoginFTUE()
  }
}
