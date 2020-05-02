package gymkapp.main.model

data class User(
  val id: String,
  val maps: MutableList<Map> = mutableListOf()
)
