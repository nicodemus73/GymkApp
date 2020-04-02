package gymkapp.main

/**
 * Clase de test (Mock) para simular el comportamiento en caso de que vaya bien
 * Las llamadas son estaticas para que se puedan acceder desde toda la app, puede que haya que cambiarlo
 */
class RemoteAPI {

  companion object{

    fun login(user:String,password:String) = Pair(false,"t0kenInventado")

    fun register(user:String,password:String) = Pair(false,"")
  }
}