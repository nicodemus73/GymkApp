package gymkapp.main.api

import android.util.Log
import gymkapp.main.BASE_URL
import gymkapp.main.api.RemoteAPI.ALGO_QUE_NO_ES_UN_MAPA
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MapsCallsClient {

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

    private fun createClient(token: String) = secureClientBuilder
      .addInterceptor { chain ->
        chain.proceed(
          chain.request()
            .newBuilder()
            .addHeader("Authorization", token)
            .build()
        )
      }.build().also { Log.d(MapsCallsClient::class.java.simpleName, token) }
  }
}