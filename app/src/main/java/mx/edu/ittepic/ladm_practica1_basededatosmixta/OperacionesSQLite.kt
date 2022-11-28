package mx.edu.ittepic.ladm_practica1_basededatosmixta

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList

class OperacionesSQLite(val activity: AppCompatActivity) {
    private val db = ConexionSQLite(activity, "datos", null, 1)

    fun getPostulante(id: String): Postulante? {
        val res = db.readableDatabase.rawQuery(
            "SELECT NOMBRE, PROCEDENCIA, CELULAR, EMAIL, OPCION1, OPCION2, ID, REGISTRADO FROM POSTULANTES " +
                    "WHERE ID = ?",
            arrayOf(id!!)
        )
        if (!res.moveToFirst()) return null
        val postulante = Postulante(
            res.getString(0),
            res.getString(1),
            res.getString(2),
            res.getString(3),
            res.getString(4),
            res.getString(5),
            res.getInt(6).toString(),
            res.getString(7)
        )
        res.close()
        return postulante
    }

    fun getPostulantes(): ArrayList<Postulante>? {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT NOMBRE, PROCEDENCIA, CELULAR, EMAIL, OPCION1, OPCION2, ID, REGISTRADO FROM POSTULANTES",
            arrayOf()
        )
        if (!cursor.moveToFirst()) return null
        val postulantes = ArrayList<Postulante>()
        do {
            postulantes.add(
                Postulante(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                )
            )
        } while (cursor.moveToNext())
        cursor.close()
        return postulantes
    }

    fun agregarPostulante(postulante: Postulante): Boolean {
        val datos = ContentValues()
        datos.put("NOMBRE", postulante.nombre)
        datos.put("PROCEDENCIA", postulante.procedencia)
        datos.put("CELULAR", postulante.celular)
        datos.put("EMAIL", postulante.email)
        datos.put("OPCION1", postulante.opcion1)
        datos.put("OPCION2", postulante.opcion2)
        datos.put("REGISTRADO", Date().toString())
        return db.writableDatabase.insert("POSTULANTES", "ID", datos) > 0
    }

    fun actualizarPostulante(postulante: Postulante): Boolean {
        val datos = ContentValues()
        datos.put("NOMBRE", postulante.nombre)
        datos.put("PROCEDENCIA", postulante.procedencia)
        datos.put("CELULAR", postulante.celular)
        datos.put("EMAIL", postulante.email)
        datos.put("OPCION1", postulante.opcion1)
        datos.put("OPCION2", postulante.opcion2)
        return db.writableDatabase.update("POSTULANTES", datos, "ID=?", arrayOf(postulante.id!!)) > 0
    }

    fun eliminarPostulante(postulante: Postulante): Boolean {
        return db.writableDatabase.delete("POSTULANTES", "ID=?", arrayOf(postulante.id!!)) > 0
    }

    fun limpiarDatos(): Boolean {
        return db.writableDatabase.delete("POSTULANTES", null, null) > 0
    }

}