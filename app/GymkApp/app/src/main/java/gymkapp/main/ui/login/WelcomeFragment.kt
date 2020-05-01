package gymkapp.main.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import gymkapp.main.databinding.WelcomeBinding

class WelcomeFragment : Fragment() {

  private var _bind: WelcomeBinding? = null
  private val bind inline get() = _bind!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    _bind = WelcomeBinding.inflate(inflater, container, false)
    return bind.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    super.onViewCreated(view, savedInstanceState)

    val navController = findNavController()
    bind.loginButton.setOnClickListener { navController.navigate(WelcomeFragmentDirections.toLogin()) }
    bind.registerButton.setOnClickListener { navController.navigate(WelcomeFragmentDirections.toRegister()) }
    Log.d(javaClass.simpleName, "Me han creado")
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _bind = null
  }
}
