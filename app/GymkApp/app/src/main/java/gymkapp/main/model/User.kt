package gymkapp.main.model

data class User(
  val id: String,
  val name: String,
  val maps: MutableList<Map> = mutableListOf(),
  val points: MutableList<Point> = mutableListOf()
)
