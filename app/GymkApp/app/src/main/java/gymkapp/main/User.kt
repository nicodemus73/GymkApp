package gymkapp.main

import com.google.gson.annotations.SerializedName

object User {

  @SerializedName("token")
  var loginToken : String? = null
  //NO SE ENVIA
  var name : String? = null
  var maps = mutableListOf<Map>()
  var points = mutableListOf<Point>()
}