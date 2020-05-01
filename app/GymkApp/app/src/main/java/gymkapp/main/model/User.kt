package gymkapp.main.model

data class User(
  val id: String,
  val maps: MutableList<Map> = mutableListOf(),
  val points: MutableList<Point> = mutableListOf()
  //var gameId: String? = null - Posible manejo por el servidor
)
