package gymkapp.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.welcome.view.*

class WelcomeFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.welcome,container,false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    view.nightModeSwitch.setOnCheckedChangeListener{
        _,isChecked ->
      if(isChecked) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
      else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    val navController = findNavController()
    view.loginButton.setOnClickListener{ navController.navigate(WelcomeFragmentDirections.toLogin()) }
    view.registerButton.setOnClickListener { navController.navigate(WelcomeFragmentDirections.toRegister()) }
    Log.d(javaClass.name,"Me han creado")
  }
}
