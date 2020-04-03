package gymkapp.main

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {

  @FormUrlEncoded
  @POST("user/registration")
  fun createUser(
    @Field("username") username: String,
    @Field("password") password: String
  ): Call<DefaulResponseLogin>

}