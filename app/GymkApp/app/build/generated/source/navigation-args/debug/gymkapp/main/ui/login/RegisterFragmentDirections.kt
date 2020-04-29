package gymkapp.main.ui.login

import androidx.navigation.NavDirections
import gymkapp.main.FTUELoginDirections

class RegisterFragmentDirections private constructor() {
  companion object {
    fun toMainGraph(): NavDirections = FTUELoginDirections.toMainGraph()

    fun toLoginFTUE(): NavDirections = FTUELoginDirections.toLoginFTUE()
  }
}
