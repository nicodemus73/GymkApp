package gymkapp.main.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import gymkapp.main.R
import kotlinx.android.synthetic.main.welcome.view.*

class WelcomeFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.welcome,container,false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    super.onViewCreated(view, savedInstanceState)

    val navController = findNavController()
    view.loginButton.setOnClickListener{ navController.navigate(WelcomeFragmentDirections.toLogin()) }
    view.registerButton.setOnClickListener { navController.navigate(WelcomeFragmentDirections.toRegister()) }
    Log.d(javaClass.simpleName,"Me han creado")
  }
}
