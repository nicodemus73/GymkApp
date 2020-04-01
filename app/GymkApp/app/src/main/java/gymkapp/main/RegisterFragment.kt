package gymkapp.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.register.view.*

class RegisterFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.register,container,false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    val navController = findNavController()
    view.buttonBack.setOnClickListener { navController.navigateUp() } //Nota: no permitir esta llamada cuando la pila solo tiene el startDestination, o gestionarla con finish()
  }
}
