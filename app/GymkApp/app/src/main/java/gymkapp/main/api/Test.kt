import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName

private data class Map(
  var metadata: Metadata,
  @SerializedName("_id")
  var id: Int,
  var name: String,
  var firstLocation: Point
)

private data class Metadata(var author: String, var description: String)
private data class Point(val id: Int, val name: String, val location: GeoJSONPoint)
private data class GeoJSONPoint(val type: String = "Point", val coordinates: List<Double>)
private data class UserInfo(val username : String, val password: String)
private data class ErrorMessage(val message: String)

fun main() {

  println("---------------------- LOGIN -------------------------")
  println(GsonBuilder().setPrettyPrinting().create().toJson(UserInfo("User","Password")))
  println(Gson().fromJson("""{"auth":"123"}""".trimIndent(),ErrorMessage::class.java))
  println("---------------------- MAPAS --------------------------")
  println(
    GsonBuilder().setPrettyPrinting().create().toJson(
      arrayOf(
        Map(
          Metadata("jo", "esto es una prueba"), 123131, "prova", Point(
            1231, "Hola",
            GeoJSONPoint("Point", listOf(2.170040, 41.386991))
          )
        )
      )
    )
  )

  val json = Gson().toJson(
    arrayOf(
      Map(
        Metadata("jo", "esto es una prueba"), 123131, "prova", Point(
          1231, "Hola",
          GeoJSONPoint("Point", listOf(2.170040, 41.386991))
        )
      )
    )
  )

  val gsonObject = Gson().fromJson(json, Array<Map>::class.java)
  gsonObject.forEach(::println)
}