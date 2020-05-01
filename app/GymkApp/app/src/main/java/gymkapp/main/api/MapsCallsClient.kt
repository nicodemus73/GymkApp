package gymkapp.main.api

import android.util.Log
import gymkapp.main.BASE_URL
import gymkapp.main.api.RemoteAPI.firstPointInfoOfAMap
import gymkapp.main.model.Point
import gymkapp.main.model.Stage
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface MapsCallsClient {

  //funcion que devuelve el primer punto de los mapas cercanos dado unas cordenadas y un radio
  @GET("/map/propers")
  suspend fun listNearMaps(
    @Query("lon") long: Double,
    @Query("lat") lat: Double,
    @Query("radius") radius: Int
  ): Response<Array<firstPointInfoOfAMap>>//  //recibo una lista de Map

  //función que devuelve información sobre el mapa
  @GET("/map/{id}")
  suspend fun infoMap(@Path("id") id: String): Response<firstPointInfoOfAMap>

  //función que te devuelve un mapa con un array de puntos (el de la demo)
  @POST("/game/demo/new")
  suspend fun obtainStartMap():Response<Stage>

  @POST("/game/demo")
  suspend fun obtainNextStageMap(@Body location: Point):Response<Stage>



  companion object Factory {

    fun create(token: String): MapsCallsClient = Retrofit.Builder()
      .addConverterFactory(GsonConverterFactory.create())
      .baseUrl(BASE_URL)
      .client(createClient(token))
      .build()
      .create(MapsCallsClient::class.java)

    private fun createClient(token: String) = secureClientBuilder
      .addInterceptor { chain ->
        chain.proceed(
          chain.request()
            .newBuilder()
            .addHeader("Authorization", token)
            .build()
        )
      }.build()//.also { Log.d(MapsCallsClient::class.java.simpleName, token) }
  }
}