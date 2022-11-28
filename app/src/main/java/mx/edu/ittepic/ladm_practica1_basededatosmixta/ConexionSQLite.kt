package mx.edu.ittepic.ladm_practica1_basededatosmixta

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ConexionSQLite(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    /*
    var nombre: String,
    var procedencia: String,
    var celular: String,
    var email: String,
    var opcion1: String,
    var opcion2: String,
    */
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE POSTULANTES(" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NOMBRE VARCHAR(100)," +
                    "PROCEDENCIA VARCHAR(400)," +
                    "CELULAR VARCHAR(20)," +
                    "EMAIL VARCHAR(400)," +
                    "OPCION1 VARCHAR(12)," +
                    "OPCION2 VARCHAR(12)," +
                    "REGISTRADO VARCHAR(100))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

}