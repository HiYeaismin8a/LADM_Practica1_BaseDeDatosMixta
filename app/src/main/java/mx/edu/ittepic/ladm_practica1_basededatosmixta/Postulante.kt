package mx.edu.ittepic.ladm_practica1_basededatosmixta

data class Postulante(
    var nombre: String,
    var procedencia: String,
    var celular: String,
    var email: String,
    var opcion1: String,
    var opcion2: String,
    var id: String?,
    var registrado: String?
) {
    override fun toString(): String {
        return "Nombre: $nombre\nProcedencia: $procedencia\nCelular: $celular\nEmail: $email\nOpcion1: $opcion1" +
                "\nOpcion2: $opcion2";
    }

    fun mapear(): HashMap<String, String> {
        return hashMapOf(
            "nombre" to nombre,
            "procedencia" to procedencia,
            "celular" to celular,
            "email" to email,
            "opcion1" to opcion1,
            "opcion2" to opcion2,
            "registrado" to registrado!!
        )
    }
}