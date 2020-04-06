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
    //private data class UserId(val user_id: String) - No le veo uso a esta respuesta

    //TODO Agrupar las llamadas (Diferentes intercaces por tipo de llamada)
    //TODO Clase Interceptor (OkHttp interceptor) permite añadir una header a cada request
    private interface ScalarResponseCalls{

      //Usar Response<String> para ver el contexto de respuesta https://github.com/square/retrofit/blob/master/CHANGELOG.md#version-260-2019-06-05
      @GET("/")
      suspend fun testThisIsHome(): String

      //Para procesar los dos casos (error - JSON o exito - Header token) solo se me ocurre procesar leer la respuesta como un string y procesarlo a posteriori
      @POST("/user/login")
      suspend fun login(@Body userinfo: UserInfo): Response<String>

      @POST("/user/register")
      suspend fun register(@Body userinfo: UserInfo): Response<String>

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

      val response = scalarAPICalls.login(UserInfo(username = user, password = password))
      var failure = !response.isSuccessful
      val message = try {
        if(failure) Gson().fromJson(response.body(),ErrorMessage::class.java).error
        else response.headers()["Authorization"]!! // "!!" asegura que no es nulo, y si lo es salta una excepcion
      } catch (e: Exception) {
        failure = true
        "Unexpected error while trying to login" //El formato de los mensajes de llegada es incorrecto
      }

      println("La llamada ha salido ${if(failure)"mal" else "bien"} y el mensaje es $message")

      return Pair(failure,message)
    }

    suspend fun register(user:String,password:String) : Pair<Boolean,String> {

      val response = scalarAPICalls.register(UserInfo(username = user, password = password))
      var failure = !response.isSuccessful //(response.code()!= 200)
      val message = try {
        if (failure) Gson().fromJson(response.body(),ErrorMessage::class.java).error
        else "You were registrated successfully" //Gson().fromJson(response.body(),UserId::class.java).user_id //aqui esta el id del usuario en caso de success (200)
      } catch (e: Exception){
        failure = true
        "Unexpected error while trying to register"
      }
      return Pair(failure,message)
    }
  }
}