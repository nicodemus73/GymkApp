package gymkapp.main

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteAPI {

  companion object{
    var flag = true
    var msg= ""
    //fun login(user:String,password:String) = Pair(true,"Esto es un mensaje de error de login")
    fun login(user:String,password:String):Pair <Boolean, String> {
      RetrofitClient.instance.createUser(user, password)
          .enqueue(object:Callback<DefaulResponseLogin>{
            override fun onFailure(call: Call<DefaulResponseLogin>, t: Throwable) {
              flag = true
              msg = "Esto es un mensaje de error de login"
            }

            override fun onResponse(call: Call<DefaulResponseLogin>, response: Response<DefaulResponseLogin>) {
               flag =false
               msg = response.headers()["Athorization"].toString()
               //msg = response.headers().get("Authorization"))
            }

          })
      return Pair(flag, msg)
    }
    fun register(user:String,password:String) = Pair(true,"Esto es un mensaje de error de registro")
  }
}