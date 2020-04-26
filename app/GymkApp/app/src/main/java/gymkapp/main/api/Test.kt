import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.maps.android.data.geojson.GeoJsonFeature
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPoint

/*private data class Map(
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
private data class ErrorMessage(val message: String)*/

fun main() {

  val layer = GeoJsonLayer(null,null)
  val point = GeoJsonPoint(LatLng(1.0,2.0))
  val properties = hashMapOf("Ocean" to "South Atlantic")
  val pointFeature = GeoJsonFeature(point,"Origin",properties,null)
  layer.addFeature(pointFeature)
  layer.features.forEach{if(it.hasProperty("Ocean")) print(it.getProperty("Ocean"))}
  println(GsonBuilder().setPrettyPrinting().create().toJson(GeoJsonPoint(LatLng(1.0,2.0))))
  layer.setOnFeatureClickListener {
    when(it){
      pointFeature -> println("Point feature clicked")
    }
  }
  with(layer.defaultPointStyle) {
    isDraggable = true
    title = "Hello"
    snippet = "I am a draggable marker"
  }
}