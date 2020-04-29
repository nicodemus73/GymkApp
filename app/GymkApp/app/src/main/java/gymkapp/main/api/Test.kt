import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import com.google.maps.android.data.geojson.GeoJsonFeature
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPoint
import gymkapp.main.api.RemoteAPI

suspend fun main() {

  /*val layer = GeoJsonLayer(null,null)
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
  }*/
  //PRIMERO!!
  //RemoteAPI.initMapsCallsClient("aefnjoafheanso")
  //RemoteAPI.listNearMaps(...)
//  RemoteAPI.initMapsCallsClient("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1ZTkwNTA3NTQ2ZWIyYzA1YTQxZTRlNWYiLCJpYXQiOjE1ODY1MTYxMDN9.Tx2kqrfQMLGQGClpCQxujoe6zWnnxy7TSe219kzRBsQ")
//  RemoteAPI.obtainStartMap()
}