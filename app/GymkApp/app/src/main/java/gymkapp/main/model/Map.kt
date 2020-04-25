package gymkapp.main.model

import com.google.gson.annotations.SerializedName

data class Map(
  val metadata: Metadata,
  @SerializedName("_id")
  var id: Int,
  val name: String,
  val points: MutableList<Point>
)

data class Metadata(var author: String, var description: String)
data class Point(val id: Int, val name: String, val location: GeoJSONPoint)
data class GeoJSONPoint(val type: String = "Point", val coordinates: List<Int>)