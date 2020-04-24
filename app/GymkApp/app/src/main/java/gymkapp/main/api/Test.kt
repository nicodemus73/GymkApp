import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName

data class Map(
  var metadata: Metadata,
  @SerializedName("_id")
  var id: Int,
  var name: String,
  var firstLocation: Point
)

data class Metadata(var author: String, var description: String)
data class Point(val id: Int, val name: String, val location: GeoJSONPoint)
data class GeoJSONPoint(val type: String = "Point", val coordinates: List<Double>)


fun main() {
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