package mx.edu.ittepic.ladm_practica1_basededatosmixta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import mx.edu.ittepic.ladm_practica1_basededatosmixta.databinding.ListaPostulantesBinding

class ListaPostulantes : AppCompatActivity() {
    lateinit var binding: ListaPostulantesBinding
    private val operacionesSQLite = OperacionesSQLite(this)
    private val operacionesFirebase = OperacionesFirebase()
    private val listaIds = ArrayList<String>()
    private val postulantes = ArrayList<Postulante>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListaPostulantesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.opcion.onItemSelectedListener = itemSelectedListener
        binding.btnNuevo.setOnClickListener {
            val intent = Intent(this@ListaPostulantes, FormularioPostulante::class.java)
            startActivity(intent)
        }
        binding.btnSincronizar.setOnClickListener { sincronizarDatos() }
    }

    private fun sincronizarDatos() {
        val postulantes = operacionesSQLite.getPostulantes()
        if (postulantes == null) {
            AlertDialog.Builder(this).setTitle("DATOS LOCALES VACIOS")
                .setMessage("NO HAY POSTULANTES EN LA BASE DE DATOS LOCAL")
                .setPositiveButton("OK") { _, _ -> }
                .show()
            return
        }
        operacionesFirebase.agregarPostulantes(postulantes)
            .addOnSuccessListener {
                operacionesSQLite.limpiarDatos()
                Toast.makeText(this, "LOS DATOS SE HAN SINCRONIZADO CORRECTAMENTE", Toast.LENGTH_LONG).show()
                mostrar()
            }
            .addOnFailureListener {
                Log.d("error", it.message!!)
                AlertDialog.Builder(this).setTitle("ERROR DE SINCRONIZACION")
                    .setMessage("¿DESEA VOLVER A INTENTARLO?")
                    .setPositiveButton("SI") { _, _ -> sincronizarDatos() }
                    .setNegativeButton("NO") { _, _ -> }
                    .show()
            }
    }

    private val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            mostrar()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}

    }

    override fun onRestart() {
        super.onRestart()
        mostrar()
    }

    private fun datosLocales(datos: ArrayList<String>) {
        operacionesSQLite.getPostulantes()?.forEach { postulante ->
            datos.add(postulante.toString())
            postulantes.add(postulante)
            listaIds.add(postulante.id!!)
        }
    }

    private fun datosNube(datos: ArrayList<String>) {
        operacionesFirebase.getPostulantes()
            .addOnSuccessListener {
                datos.clear()
                it.documents.forEach { doc ->
                    val postulante = Postulante(
                        doc.getString("nombre")!!,
                        doc.getString("procedencia")!!,
                        doc.getString("celular")!!,
                        doc.getString("email")!!,
                        doc.getString("opcion1")!!,
                        doc.getString("opcion2")!!,
                        doc.id,
                        doc.getString("registrado")
                    )
                    datos.add(postulante.toString())
                    postulantes.add(postulante)
                    listaIds.add(doc.id)
                }

                if (datos.size == 0) {
                    datos.add("NO HAY POSTULANTES")
                    return@addOnSuccessListener
                }
                binding.postulantes.adapter =
                    ArrayAdapter(this@ListaPostulantes, android.R.layout.simple_list_item_1, datos)
                listenerPostulantes()
            }
            .addOnFailureListener {
                Log.d("error", it.message!!)
                AlertDialog.Builder(this).setTitle("ERROR DE CONSULTA")
                    .setMessage("NO SE HA PODIDO CONSULTAR LOS DATOS DE FORMA CORRECTA, REVISE SU CONEXION")
                    .setPositiveButton("OK") { _, _ -> }
            }
    }

    private fun mostrar() {
        postulantes.clear()
        val datos = ArrayList<String>()
        var local = false
        when (binding.opcion.selectedItem.toString()) {
            "Local" -> {
                datosLocales(datos)
                local = true
            }
            "Nube" -> datosNube(datos)
        }
        binding.postulantes.adapter =
            ArrayAdapter(this@ListaPostulantes, android.R.layout.simple_list_item_1, datos)

        if (datos.size == 0) {
            datos.add("NO HAY POSTULANTES")
            binding.postulantes.onItemClickListener = null
            return
        }

        listenerPostulantes(local)

    }

    private fun listenerPostulantes(local: Boolean = false) {
        binding.postulantes.setOnItemClickListener { _, _, position, _ ->
            AlertDialog.Builder(this).setTitle("OPERACIONES")
                .setMessage("QUE DESEA HACER CON: ${postulantes[position].nombre}")
                .setPositiveButton("ACTUALIZAR") { _, _ -> actualizar(postulantes[position], local) }
                .setNegativeButton("ELIMINAR") { _, _ -> eliminar(postulantes[position], local) }
                .setNeutralButton("NADA") { _, _ -> }
                .show()
        }
    }

    private fun eliminar(postulante: Postulante, local: Boolean) {
        if (local) {
            if (operacionesSQLite.eliminarPostulante(postulante)) {
                Toast.makeText(this, "ELIMINADO CORRECTAMENTE", Toast.LENGTH_LONG).show()
                mostrar()
            } else {
                AlertDialog.Builder(this).setTitle("ERROR")
                    .setMessage("NO SE HA ELIMINADO EL POSTULANTE. ¿INTENTAR DE NUEVO?")
                    .setPositiveButton("SI") { _, _ -> eliminar(postulante, local) }
                    .setNegativeButton("NO") { _, _ -> }
                    .show()
            }
        } else {
            operacionesFirebase.eliminarPostulante(postulante)
                .addOnSuccessListener {
                    Toast.makeText(this, "ELIMINADO CORRECTAMENTE", Toast.LENGTH_LONG).show()
                    mostrar()
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this).setTitle("ERROR")
                        .setMessage("NO SE HA ELIMINADO EL POSTULANTE. ¿INTENTAR DE NUEVO?")
                        .setPositiveButton("SI") { _, _ -> eliminar(postulante, local) }
                        .setNegativeButton("NO") { _, _ -> }
                        .show()
                }
        }

    }

    private fun actualizar(postulante: Postulante, local: Boolean) {
        val intent = Intent(this, FormularioPostulante::class.java).apply {
            putExtra("ID", postulante.id!!)
            putExtra("local", local)
        }
        startActivity(intent)
    }
}