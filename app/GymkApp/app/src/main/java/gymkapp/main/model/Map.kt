package gymkapp.main.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Cada mapa representa una Gymkhana diferente
 * El id representa su identificador unico (en la bbdd)
 * Tiene una vista previa
 * Tiene una lista de tests que el jugador debe superar
 */
data class Map(
  @SerializedName("_id")
  var id: Int? = null, //TODO seguramente no sean integers
  @Expose(deserialize = false)
  val preview: MapPreview,
  val tests: MutableList<Test> = mutableListOf()
)

/**
 * Clase que representa la información de preview que se dara al usuario sobre una Gymkhana
 * El id será la identificación del mapa. Será null hasta que se haya guardado en la base de datos
 * El autor será el nombre del creador del mapa
 * La descripción del mapa
 * El nombre del mapa (puede estar repetido)
 */
data class MapPreview(
  @SerializedName("_id")//Supongo?
  var id: Int? = null,
  val author: String,
  var description: String,
  var name: String,
  var startingPoint: LatLng
)

//data class Metadata(var author: String, var description: String)
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