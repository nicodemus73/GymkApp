package gymkapp.main.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import gymkapp.main.api.RemoteAPI.infoMap
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

object RemoteAPI {

    private const val baseUrl = "http://10.4.41.144:3001"

    private data class UserInfo(val username: String, val password: String)
    private data class ErrorMessage(val error: String)
    //private data class NearPoint(val location: GeoJSONPoint, val radius: Int) //GeoJSONPoint
    data class Metadata(var author: String, var description: String)
    data class Map(
        var metadata: Metadata,
        @SerializedName("_id")
        var id: String,
        var name: String,
        var firstLocation: GeoJSONPoint//MutableList<GeoJSONPoint>
    )


    data class Point(val id: String, val name: String, val location: GeoJSONPoint)
    data class GeoJSONPoint(var type: String/* = "Point"*/, var coordinates: List<Double>)
    //private data class UserId(val user_id: String) - No le veo uso a esta respuesta

    //TODO Agrupar las llamadas (Diferentes intercaces por tipo de llamada)
    //TODO Clase Interceptor (OkHttp interceptor) permite añadir una header a cada request
    private interface ScalarResponseCalls {

        //Usar Response<String> para ver el contexto de respuesta https://github.com/square/retrofit/blob/master/CHANGELOG.md#version-260-2019-06-05
        @GET("/")
        suspend fun testThisIsHome(): String

        //Para procesar los dos casos (error - JSON o exito - Header token) solo se me ocurre procesar leer la respuesta como un string y procesarlo a posteriori
        @POST("/user/login")
        suspend fun login(@Body userinfo: UserInfo): Response<String>

        @POST("/user/register")
        suspend fun register(@Body userinfo: UserInfo): Response<String>

        @GET("/map")
        suspend fun listNearMaps(
            @Query("lon") long: Double,
            @Query("lat") lat: Double,
            @Query("radius") radius: Int
        ): Response<Array<Map>>//  //recibo una lista de Map

        @GET("/map" +"/5ea2e44069770d5b5eab2e1d")//{id}")
        suspend fun infoMap(/*@Path("id") id:String */):Response<Map>


        companion object Factory {

            fun create(/*token: String*/): ScalarResponseCalls = Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .client(createClient())//que passa quan no es necessita token ex login register?? CREC que res ja que no comproba el token alla
                .build()
                .create(ScalarResponseCalls::class.java)

            fun createClient(/*token: String*/): OkHttpClient {
                var token =
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1ZTkwNTA3NTQ2ZWIyYzA1YTQxZTRlNWYiLCJpYXQiOjE1ODY1MTYxMDN9.Tx2kqrfQMLGQGClpCQxujoe6zWnnxy7TSe219kzRBsQ"//getToken()
                println(token)
                return OkHttpClient.Builder().addInterceptor { chain ->
                    val builder = chain.request().newBuilder()
                    builder.addHeader("Authorization", token)
                    val request = builder.build()
                    chain.proceed(request)
                }.build()
            }
        }
    }

    private val scalarAPICalls =
        ScalarResponseCalls.create(/*token: String*/)

    suspend fun login(user: String, password: String): Pair<Boolean, String> {

        val tag = javaClass.simpleName
        //Arreglo temporal, si tenemos que añadir mas excepciones tendriamos que hacerlo un poco mas limpio. Podriamos omitir lo de la VPN para la entrega.
        Log.d(tag, "el usuario es $user, la contraseña es $password")
        val response = try {
            scalarAPICalls.login(
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
                Log.d(tag, "Intento leer el JSON")
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
            tag,
            "La llamada ha salido ${if (failure) "mal" else "bien"} y el mensaje es $message"
        )

        return Pair(failure, message)
    }

    suspend fun register(user: String, password: String): Pair<Boolean, String> {

        val response = try {
            scalarAPICalls.register(
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
            else "You were registrated successfully"
        } catch (e: Exception) {
            failure = true
            "Unexpected error while trying to register"
        }
        return Pair(failure, message)
    }

    //suspend fun listNearMaps(location: GeoJSONPoint, radio: Int): Pair<Pair<Boolean, String>, Array<Map> >{ //afegir els maps dins d'una llista, (demanar al server el numero de maps?)
    suspend fun listNearMaps(lon: Double, lat: Double, radius: Int): Pair<Pair<Boolean, String>, Array<Map>> {
        //al inci el vull en null
        var NearMap = arrayOf<Map>()
        val response = try {
            scalarAPICalls.listNearMaps(
                long = lon, lat = lat, radius = radius
            )
        } catch (e: Exception) {
            println(e.message)
            return Pair(Pair(true, "Can't connect to the server"), NearMap)
        }
        println("url: " + response.raw().request().url())
        var failure = !response.isSuccessful //(response.code()!= 200)
        println(failure)
        val message = try {
            if (failure) Gson().fromJson(
                response.errorBody()?.charStream()?.readText(),
                ErrorMessage::class.java
            ).error
            else {
                println("hola")
                //guardar els mapes propers
                println( Gson().fromJson(response.body().toString(),Array<Map>::class.java))
                NearMap = (Gson().fromJson(response.body().toString(), Array<Map>::class.java))
                println("no error")
                "no hay error"
            }
        } catch (e: Exception) {
            println(e.message)
            failure = true
            "Unexpected error while trying to load near maps"
        }
        println("La llamada ha salido ${if (failure) "mal" else "bien"} y el mensaje es $message")
        NearMap.forEach(::println)
        return Pair(Pair(failure, message), NearMap)
    }
     suspend fun infoMap (/*Id: String*/) : /*Pair<*/Pair<Boolean, Any>/*, Map>*/ {
          var information: Map? = null
          //var information: Map
          val response = try {
              //println(Id)
              scalarAPICalls.infoMap()
          } catch (e: Exception) {
              println("error")
              println(e.message)
              return /*Pair(*/Pair(true, "Can't connect to the server")/*, information)*/
          }
          println("url: " + response.raw().request().url())
          var failure = !response.isSuccessful //(response.code()!= 200)
          val message = try {
              println("llegint message")
              if (failure) Gson().fromJson(
                  response.errorBody()?.charStream()?.readText(),
                  ErrorMessage::class.java
              ).error
              else {
                  //guardar els mapes propers
                 println( Gson().toJson(response.body()))
                   information = Gson().fromJson(response.body().toString(), Map::class.java)
                  "no hay error"
              }
          } catch (e: Exception) {
              println(e.message)
              failure = true
              "Unexpected error while trying to load near maps"
          }
          println("La llamada ha salido ${if (failure) "mal" else "bien"} y el mensaje es $message")
          println(information)
          return /*Pair(*/Pair(failure, message)/*, information)*/
      }


}
suspend fun main (){
    //val enviar = RemoteAPI.GeoJSONPoint("Point", listOf(2.170040,41.386991))
    /*var lon = 2.170040
    var lat = 41.386991
    val radius = 300*/
    println("abans d'entrar")
    infoMap()
    //listNearMaps(lon, lat, radius)
    //val Id = "5ea2e44069770d5b5eab2e1d"
}
