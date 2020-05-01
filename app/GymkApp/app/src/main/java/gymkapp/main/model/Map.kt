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
data class GeoJSONPoint(var type: String = "Point", var coordinates: List<Double>)
//per cridar /game/demo -> obtainNextStageMap se li ha de passar un Point
data class Point (var location: GeoJSONPoint)

//Stage es el resultat de cridar /game/demo/new -> obtainStartMap() o /game/demo -> obtainNextStageMap
data class Stage(
  var message: String,
  var location: GeoJSONPoint,
  var time: String,
  var error: String
)


