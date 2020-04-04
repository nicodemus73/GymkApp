package gymkapp.main

import com.google.gson.Gson
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.lang.Exception

class RemoteAPI {

  companion object{

    private const val baseUrl = "http://10.4.41.144:3001"
    private data class UserInfo(val username: String, val password: String)
    private data class ErrorMessage(val error: String)

    //TODO Agrupar las llamadas (Diferentes intercaces por tipo de llamada)
    //TODO Clase Interceptor (OkHttp interceptor) permite añadir una header a cada request
    private interface ScalarResponseCalls{

      //Usar Response<String> para ver el contexto de respuesta https://github.com/square/retrofit/blob/master/CHANGELOG.md#version-260-2019-06-05
      @GET("/")
      suspend fun testThisIsHome(): String

      //Para procesar los dos casos (error - JSON o exito - Header token) solo se me ocurre procesar leer la respuesta como un string y procesarlo a posteriori
      @POST("/user/login")
      suspend fun login(@Body userinfo: UserInfo): Response<String>

      companion object Factory {

        fun create(): ScalarResponseCalls = Retrofit.Builder()
          .addConverterFactory(ScalarsConverterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .baseUrl(baseUrl)
          .build()
          .create(ScalarResponseCalls::class.java)
      }
    }

    private val scalarAPICalls = ScalarResponseCalls.create()

    suspend fun login(user:String, password:String) : Pair<Boolean,String> {

      /*println(scalarAPICalls.testThisIsHome())
      if(token!=null) println("El token es: $token y el codigo http es: ${response.code()}")
      else println("El mensaje de error es: ${Gson().fromJson(response.body(),ErrorMessage::class.java).error} y el codigo http es: ${response.code()}")
      return Pair(token==null, "Esto es un mensaje de error de login")*/
      val response = scalarAPICalls.login(UserInfo(username = user, password = password))
      val token = response.headers()["Authorization"]
      val failure = token==null
      val message = try {
        if(failure) Gson().fromJson(response.body(),ErrorMessage::class.java).error else token ?: "null"
      } catch (e: Exception){ "Error inesperado" }
      //println("failure: $failure message: $message")
      return Pair(failure,message)
    }
    fun register(user:String,password:String) = Pair(true,"Esto es un mensaje de error de registro")
  }
}