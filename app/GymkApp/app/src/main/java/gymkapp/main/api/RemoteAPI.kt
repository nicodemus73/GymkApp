package gymkapp.main.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import gymkapp.main.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

object RemoteAPI {

  private val classTag = javaClass.simpleName

  private data class UserInfo(val username: String, val password: String)
  private data class ErrorMessage(val error: String)

  //TODO mover
  data class Map(
    var metadata: Metadata,
    @SerializedName("_id")
    var id: Int,
    var name: String,
    var firstLocation: MutableList<Point>
  )
  data class Metadata(var author: String, var description: String)
  data class Point(val id: Int, val name: String, val location: GeoJSONPoint)
  data class GeoJSONPoint(val type: String = "Point", val coordinates: List<Double>)

  //Los Log.d pueden filtrarse con ((Login|Welcome|Settings|Register|Maps|Social)(Model|ViewModel|Fragment)|MainActivity|RemoteAPI) como regex
  //TODO Clase Interceptor (OkHttp interceptor) permite añadir una header a cada request
  private interface AuthenticationCallsClient {

    //Usar Response<String> para ver el contexto de respuesta https://github.com/square/retrofit/blob/master/CHANGELOG.md#version-260-2019-06-05
    @POST("/user/login")
    suspend fun login(@Body userinfo: UserInfo): Response<String>

    @POST("/user/register")
    suspend fun register(@Body userinfo: UserInfo): Response<String>

    companion object Factory {

      fun create(): AuthenticationCallsClient = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
        .create(AuthenticationCallsClient::class.java)
    }
  }

  private interface MapsCallsClient {

    @GET("/map")
    suspend fun listNearMaps(
      @Query("location") location: GeoJSONPoint,
      @Query("radius") radio: Int
    ): Response<Array<Map>>

    companion object Factory {

      fun create(token: String): MapsCallsClient = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(createClient(token))
        .build()
        .create(MapsCallsClient::class.java)

      private fun createClient(token: String) = OkHttpClient.Builder()
        .addInterceptor { chain ->
          chain.proceed(
            chain.request()
              .newBuilder()
              .addHeader("Authorization", token)
              .build()
          )
        }.build().also { Log.d(classTag,token) }
    }
  }

  private val authCalls = AuthenticationCallsClient.create()
  private lateinit var mapsCalls: MapsCallsClient

  fun createMapsCallsClient(token: String){
    mapsCalls = MapsCallsClient.create(token)
  }

  suspend fun login(user: String, password: String): Pair<Boolean, String> {

    Log.d(classTag, "el usuario es $user, la contraseña es $password")
    val response = try {
      authCalls.login(
        UserInfo(
          username = user,
          password = password
        )
      )
    } catch (e: Exception) {
      return Pair(true, "Can't connect to the server")
    }
    var failure = !response.isSuccessful
    val message = try {
      if (failure) {
        Log.d(classTag, "Intento leer el JSON")
        Gson().fromJson(
          response.errorBody()?.charStream()?.readText(),
          ErrorMessage::class.java
        ).error
      } else response.headers()["Authorization"]!! // "!!" asegura que no es nulo, y si lo es salta una excepcion
    } catch (e: Exception) {
      failure = true
      "Unexpected error while trying to login" //El formato de los mensajes de llegada es incorrecto
    }

    Log.d(
      classTag,
      "La llamada ha salido ${if (failure) "mal" else "bien"} y el mensaje es $message"
    )

    return Pair(failure, message)
  }

  suspend fun register(user: String, password: String): Pair<Boolean, String> {

    val response = try {
      authCalls.register(
        UserInfo(
          username = user,
          password = password
        )
      )
    } catch (e: Exception) {
      return Pair(true, "Can't connect to the server")
    }
    var failure = !response.isSuccessful //(response.code()!= 200)
    val message = try {
      if (failure) Gson().fromJson(
        response.errorBody()?.charStream()?.readText(),
        ErrorMessage::class.java
      ).error
      else "You were registrated successfully" //Gson().fromJson(response.body(),UserId::class.java).user_id //aqui esta el id del usuario en caso de success (200)
    } catch (e: Exception) {
      failure = true
      "Unexpected error while trying to register"
    }
    return Pair(failure, message)
  }

  //suspend fun listNearMaps(location: GeoJSONPoint, radio: Int): Pair<Pair<Boolean, String>, Array<Map> >{ //afegir els maps dins d'una llista, (demanar al server el numero de maps?)
  suspend fun listNearMaps(
    location: GeoJSONPoint,
    radio: Int
  ): Pair<String?, Array<Map>?> {
    if(!::mapsCalls.isInitialized) return Pair("Error interno",null).also { Log.d(classTag,"El cliente no se ha inicializado") }

    val response = try {
      mapsCalls.listNearMaps(
        location, radio
      )
    } catch (e: Exception) {
      Log.d(classTag, e.message.toString())
      return Pair("Can't Connect to the server", null)
    }

    Log.d(classTag, "url: " + response.raw().request().url())
    return if (response.isSuccessful) response.body()?.let { Pair(null, it) }
      ?: Pair("Error inesperado", null)
    else response.errorBody()?.let { Pair(it.charStream().readText(), null) }
      ?: Pair("Error inesperado", null)
  }
}
/*suspend fun main (){
    val enviar = RemoteAPI.GeoJSONPoint("Point", listOf(2.170040, 41.386991))
    val radius = 300
    println("abans d'entrar")
    listNearMaps(enviar, radius)
}*/
