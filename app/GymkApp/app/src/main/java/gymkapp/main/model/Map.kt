package gymkapp.main.model

import com.google.android.gms.maps.model.LatLng

/**
 * Cada mapa representa una Gymkhana diferente
 */
data class Map(
  val info: Metadata,
  var id: Int? = null,
  var name: String,
  val tests: MutableList<Test> = mutableListOf()
)

data class Metadata(var author: String, var description: String)
data class Test(var description: String, var point: LatLng)

@Deprecated("No se necesita esta clase en el modelo")
data class GeoJSONPoint(var type: String = "Point", var coordinates: List<Double>)
@Deprecated("No se necesita esta clase en el modelo")
data class Point(var location: GeoJSONPoint)
@Deprecated("No se necesita esta clase en el modelo")
data class Stage(
  var message: String,
  var location: GeoJSONPoint,
  var time: String,
  var error: String
)