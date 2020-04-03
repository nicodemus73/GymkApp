package gymkapp.main

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import kotlin.concurrent.thread

class RemoteAPI {

  companion object{

    private const val baseUrl = "http://10.4.41.144:3001"

    //TODO Agrupar las llamadas (Diferentes intercaces por tipo de llamada)
    //TODO Clase Interceptor (OkHttp interceptor) permite añadir una header a cada request
    private interface TestServerCalls{

      //Usar Response<String> para ver el contexto de respuesta https://github.com/square/retrofit/blob/master/CHANGELOG.md#version-260-2019-06-05
      @GET("/")
      suspend fun testThisIsHome(): String

      companion object Factory {

        fun create(): TestServerCalls = Retrofit.Builder()
          .addConverterFactory(ScalarsConverterFactory.create())
          .baseUrl(baseUrl)
          .build()
          .create(TestServerCalls::class.java)
      }
    }

    private val serviceAPICalls = TestServerCalls.create()

    suspend fun login(user:String, password:String) : Pair<Boolean,String> {

      println(serviceAPICalls.testThisIsHome())
      return Pair(true, "Esto es un mensaje de error de login")
    }
    fun register(user:String,password:String) = Pair(true,"Esto es un mensaje de error de registro")
  }
}