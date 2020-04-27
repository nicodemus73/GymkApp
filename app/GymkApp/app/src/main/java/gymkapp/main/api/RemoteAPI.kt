package gymkapp.main.api

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import gymkapp.main.BASE_URL
import gymkapp.main.DEFAULT_VIEW_RADIUS
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object RemoteAPI {

  private val classTag = javaClass.simpleName

  //DATOS ENVIADOS
  private data class UserInfo(val username: String, val password: String)

  //DATOS RECIBIDOS
  private data class ErrorMessage(val error: String)

  //TODO (mover y) RENOMBRAR + renombrar otras variables relacionadas (map,maps) y todo lo relacionado con mapas que no sea un mapa...
  data class ALGO_QUE_NO_ES_UN_MAPA(
    var metadata: Metadata,
    @SerializedName("_id")
    var id: String,
    var name: String,
    var firstLocation: GeoJSONPoint//MutableList<GeoJSONPoint>
  )

  data class Metadata(var author: String, var description: String)
  data class GeoJSONPoint(var type: String = "Point", var coordinates: List<Double>)

  //Los Log.d pueden filtrarse con ((Login|Welcome|Settings|Register|Maps|Social)(Model|ViewModel|Fragment)|MainActivity|RemoteAPI) como regex
  //TODO Clase Interceptor (OkHttp interceptor) permite añadir una header a cada request
  //TODO borrar escalares del gradle si no los utilizamos
  private interface AuthenticationCallsClient {

    //Usar Response<String> para ver el contexto de respuesta https://github.com/square/retrofit/blob/master/CHANGELOG.md#version-260-2019-06-05
    @POST("/user/login")
    suspend fun login(@Body userinfo: UserInfo): Response<Unit>

    @POST("/user/register")
    suspend fun register(@Body userinfo: UserInfo): Response<Unit>

    companion object Factory {

      fun create(): AuthenticationCallsClient = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(createSecureClient())
        .build()
        .create(AuthenticationCallsClient::class.java)
    }
  }

  private interface MapsCallsClient {

    @GET("/map")
    suspend fun listNearMaps(
      @Query("lon") long: Double,
      @Query("lat") lat: Double,
      @Query("radius") radius: Int
    ): Response<Array<ALGO_QUE_NO_ES_UN_MAPA>>//  //recibo una lista de Map

    @GET("/map/{id}")
    suspend fun infoMap(@Path("id") id: String): Response<ALGO_QUE_NO_ES_UN_MAPA>

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
        }.build().also { Log.d(classTag, token) }
    }
  }

  private val authCalls by lazy { AuthenticationCallsClient.create() }
  private lateinit var mapsCalls: MapsCallsClient

  fun createMapsCallsClient(token: String) {
    mapsCalls = MapsCallsClient.create(token)
  }

  private fun parseError(errorBody: ResponseBody) =
    Gson().fromJson(errorBody.charStream().readText(), ErrorMessage::class.java).error

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
      Log.d(classTag, "${e.message}")
      return Pair(true, "Can't connect to the server")
    }

    var failure = !response.isSuccessful
    val message = try {
      if (failure) parseError(response.errorBody()!!)
      else response.headers()["Authorization"]!!
    } catch (e: Exception) {
      failure = true
      "Error interno"
    }
    return Pair(failure, message)
  }

  suspend fun register(user: String, password: String): Pair<Boolean, String> {

    val response = try {
      authCalls.register(UserInfo(user, password))
    } catch (e: Exception) {
      return Pair(true, "Can't connect to the server")
    }

    var failure = !response.isSuccessful
    val message = try {
      if (failure) parseError(response.errorBody()!!)
      else "You were registrated successfully"
    } catch (e: Exception) {
      failure = true
      "Unexpected error while trying to register"
    }

    return Pair(failure, message)
  }

  //suspend fun listNearMaps(location: GeoJSONPoint, radio: Int): Pair<Pair<Boolean, String>, Array<Map> >{ //afegir els maps dins d'una llista, (demanar al server el numero de maps?)
  /**
   * Devuelve la lista de mapas cercanos
   * Si sale bien, first es nulo y second contiene la lista
   * Si no, first contiene el mensaje de error y second es nulo
   */
  suspend fun listNearMaps(
    center: LatLng,
    radio: Int = DEFAULT_VIEW_RADIUS
  ): Pair<String?, Array<ALGO_QUE_NO_ES_UN_MAPA>?> {

    //Asegurar que el cliente se ha inicializado
    if (!::mapsCalls.isInitialized) return Pair("Error interno", null).also {
      Log.d(
        classTag,
        "El cliente no se ha inicializado"
      )
    }

    //Asegurar la conexion
    val response = try {
      mapsCalls.listNearMaps(
        long = center.longitude,
        lat = center.latitude,
        radius = radio
      )
    } catch (e: Exception) {
      Log.d(classTag, "${e.message}")
      return Pair("Can't Connect to the server", null)
    }

    //Tratar errores y la respuesta
    Log.d(classTag, "url: " + response.raw().request().url())

    var maps: Array<ALGO_QUE_NO_ES_UN_MAPA>? = null
    val message = try {
      if (response.isSuccessful) {
        maps = response.body()!!
        null
      } else parseError(response.errorBody()!!)
    } catch (e: Exception) {
      "Error inesperado"
    }
    return Pair(message, maps)
  }


  suspend fun infoMap(Id: String): Pair<String?, ALGO_QUE_NO_ES_UN_MAPA?> {

    val response = try {
      mapsCalls.infoMap(id = Id)
    } catch (e: Exception) {
      Log.d(classTag, "error")
      Log.d(classTag, "${e.message}")
      return Pair("Can't connect to the server", null)
    }

    Log.d(classTag, "url: " + response.raw().request().url())
    var map: ALGO_QUE_NO_ES_UN_MAPA? = null
    val message = try {
      Log.d(classTag, "llegint message")
      if (!response.isSuccessful) parseError(response.errorBody()!!)
      else {
        map = response.body()!!
        null
      }
    } catch (e: Exception) {
      Log.d(classTag, "${e.message}")
      "Unexpected error while trying to load near maps"
    }
    Log.d(
      classTag,
      "La llamada ha salido ${if (!response.isSuccessful) "mal y el mensaje de error es $message" else "bien"}"
    )
    return Pair(message, map)
  }
}