package gymkapp.main

data class Map(
  val metadata: Metadata,
  var id: Int,
  val name: String,
  val points: MutableList<Point>
)

data class Metadata(var author: String, var description: String)
data class Point(val id: Int, val name: String, val location: GeoJSONPoint)
data class GeoJSONPoint(val type: String = "Point", val coordinates: List<Int>)

val mapa: String = """
  {
    "metadata" : {
    "author": "autor",
    "description": "..."
    },
    "_id": "...",
    "name": "alejandro",
    "points" : [ {
    
      "_id": "..",
      "name": "..",
      "location": {
        "type": "Point",
        "coordinates": [ 12.231, 123123.12312 ]
      }
    }]
  }
""".trimIndent()