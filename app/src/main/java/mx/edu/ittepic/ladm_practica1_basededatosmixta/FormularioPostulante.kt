package mx.edu.ittepic.ladm_practica1_basededatosmixta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import mx.edu.ittepic.ladm_practica1_basededatosmixta.databinding.FormularioPostulanteBinding

class FormularioPostulante : AppCompatActivity() {
    lateinit var binding: FormularioPostulanteBinding
    var id: String = ""
    var registrado: String? = ""
    private val operacionesFirebase = OperacionesFirebase()
    private val operacionesSQLite = OperacionesSQLite(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FormularioPostulanteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cancelar.setOnClickListener { finish() }
        binding.btnRegistrar.setOnClickListener { registrarPostulante() }
        if (intent.extras != null) {
            id = intent.extras!!.get("ID").toString()
            binding.checkValidarNube.isChecked = !intent.extras!!.get("local").toString().toBoolean()
            binding.checkValidarNube.isVisible = false
            binding.btnRegistrar.setText("ACTUALIZAR")
            binding.btnRegistrar.setOnClickListener { actualizarPostulante() }
            setPostulante(!binding.checkValidarNube.isChecked)
        }

        /**
         * Obtener la fecha y hora actual con simpledate format en español : EEEE, dd/MMMM/yyyy hh:mm:ss: miércoles 26/mayo/2021 11:01
         * DateTimeFormatter fecha6 = DateTimeFormatter.ofPattern("EEEE dd/MMMM/yyyy hh:mm");
        System.out.println("EEEE, dd/MMMM/yyyy hh:mm:ss: " + fecha6.format(LocalDateTime.now()));


         */

    }

    private fun setPostulante(local: Boolean) {
        if (local) {
            postulanteLocal()
            return
        }
        postulanteNube()
    }

    private fun postulanteNube() {
        operacionesFirebase.getPostulante(id)
            .addOnSuccessListener {
                binding.nombre.setText(it.getString("nombre")!!)
                binding.procedencia.setText(it.getString("procedencia")!!)
                binding.celular.setText(it.getString("celular")!!)
                binding.inputEmail.setText(it.getString("email")!!)
                registrado = it.getString("registrado")
                val opcion1 = it.getString("opcion1")!!
                val opcion2 = it.getString("opcion2")!!
                (0 until binding.carrera1.count).forEach { i ->
                    if (binding.carrera1.getItemAtPosition(i).equals(opcion1)) {
                        binding.carrera1.setSelection(i)
                        return@forEach
                    }
                }
                (0 until binding.carrera2.count).forEach { i ->
                    if (binding.carrera2.getItemAtPosition(i).equals(opcion2)) {
                        binding.carrera2.setSelection(i)
                        return@forEach
                    }
                }
                id = it.id
            }
            .addOnFailureListener {
                Log.d("error", it.message!!)
                AlertDialog.Builder(this).setTitle("ERROR")
                    .setMessage("NO SE HA PODIDO RECUPERAR AL POSTULANTE")
                    .setPositiveButton("OK") { _, _ -> finish() }
                    .show()
            }
    }

    private fun postulanteLocal() {
        val postulante = operacionesSQLite.getPostulante(id)
        if (postulante == null) {
            AlertDialog.Builder(this).setTitle("ERROR")
                .setMessage("NO SE HA ENCONTRADO EL POSTULANTE")
                .setNeutralButton("CERRAR") { _, _ ->
                    limpiarCampos()
                    finish()
                }.show()
            return
        }
        binding.nombre.setText(postulante.nombre)
        binding.procedencia.setText(postulante.procedencia)
        binding.celular.setText(postulante.celular)
        binding.inputEmail.setText(postulante.email)
        (0 until binding.carrera1.count).forEach { i ->
            if (binding.carrera1.getItemAtPosition(i).equals(postulante.opcion1)) {
                binding.carrera1.setSelection(i)
                return@forEach
            }
        }
        (0 until binding.carrera2.count).forEach { i ->
            if (binding.carrera2.getItemAtPosition(i).equals(postulante.opcion2)) {
                binding.carrera2.setSelection(i)
                return@forEach
            }
        }
    }

    private fun actualizarPostulante() {
        if (validarCarreras()) {
            if (!binding.checkValidarNube.isChecked) {
                actualizarLocal()
                return
            }
            actualizarNube()
        } else {
            AlertDialog.Builder(this).setTitle("CARRERAS SIN SELECCIONAR")
                .setMessage("FAVOR DE SELECCIONAR CARRERAS DE LA LISTA")
                .show()
        }
    }

    private fun actualizarNube() {
        operacionesFirebase.actualizarPostulante(getPostulante())
            .addOnSuccessListener {
                AlertDialog.Builder(this).setTitle("ACTUALIZACION EXITOSA")
                    .setMessage("POSTULANTE ACTUALIZADO")
                    .setPositiveButton("OK") { _, _ ->
                        limpiarCampos()
                        finish()
                    }
                    .show()
            }
            .addOnFailureListener {
                Log.d("error", it.message!!)
                AlertDialog.Builder(this).setTitle("ERROR")
                    .setMessage("ERROR EN LA ACTUALIZACIÓN DE DATOS. ¿DESEA INTENTARLO DE NUEVO?")
                    .setPositiveButton("SI") { _, _ -> actualizarPostulante() }
                    .setNegativeButton("NO") { _, _ -> }
                    .show()
            }
    }

    private fun actualizarLocal() {
        if (operacionesSQLite.actualizarPostulante(getPostulante())) {
            AlertDialog.Builder(this).setTitle("ACTUALIZACION EXITOSA")
                .setMessage("POSTULANTE ACTUALIZADO")
                .setPositiveButton("OK") { _, _ ->
                    limpiarCampos()
                    finish()
                }
                .show()
        } else {
            AlertDialog.Builder(this).setTitle("ERROR")
                .setMessage("ERROR EN LA ACTUALIZACIÓN DE DATOS. ¿DESEA INTENTARLO DE NUEVO?")
                .setPositiveButton("SI") { _, _ -> actualizarPostulante() }
                .setNegativeButton("NO") { _, _ -> }
                .show()
        }
    }

    private fun registrarPostulante() {
        if (validarCarreras()) {
            if (!binding.checkValidarNube.isChecked) {
                registrarLocal()
                return
            }
            registrarNube()
        } else {
            AlertDialog.Builder(this).setTitle("CARRERAS SIN SELECCIONAR")
                .setMessage("FAVOR DE SELECCIONAR CARRERAS DE LA LISTA")
                .show()
        }
    }

    private fun registrarLocal() {
        if (operacionesSQLite.agregarPostulante(getPostulante())) {
            AlertDialog.Builder(this).setTitle("REGISTRO EXITOSO")
                .setMessage("¿DESEA REGISTRAR OTRO POSTULANTE?")
                .setPositiveButton("SI") { _, _ -> limpiarCampos() }
                .setNegativeButton("NO") { _, _ ->
                    limpiarCampos()
                    finish()
                }.show()
        } else {
            AlertDialog.Builder(this).setTitle("ERROR")
                .setMessage("ERROR EN LA INSERCION DE DATOS. ¿DESEA INTENTARLO DE NUEVO?")
                .setPositiveButton("SI") { _, _ -> registrarPostulante() }
                .setNegativeButton("NO") { _, _ -> }.show()
        }
    }

    private fun registrarNube() {
        operacionesFirebase.agregarPostulante(getPostulante())
            .addOnSuccessListener {
                AlertDialog.Builder(this).setTitle("REGISTRO EXITOSO")
                    .setMessage("¿DESEA REGISTRAR OTRO POSTULANTE?")
                    .setPositiveButton("SI") { _, _ -> limpiarCampos() }
                    .setNegativeButton("NO") { _, _ ->
                        limpiarCampos()
                        finish()
                    }.show()
            }
            .addOnFailureListener {
                AlertDialog.Builder(this).setTitle("ERROR")
                    .setMessage("ERROR EN LA INSERCION DE DATOS. ¿DESEA INTENTARLO DE NUEVO?")
                    .setPositiveButton("SI") { _, _ -> registrarPostulante() }
                    .setNegativeButton("NO") { _, _ -> }.show()
            }
    }

    private fun getPostulante(): Postulante {
        Log.d("id", id)
        return Postulante(
            binding.nombre.text.toString(),
            binding.procedencia.text.toString(),
            binding.celular.text.toString(),
            binding.inputEmail.text.toString(),
            binding.carrera1.selectedItem.toString(),
            binding.carrera2.selectedItem.toString(),
            if (id.length > 0) id else null,
            registrado
        )
    }

    private fun limpiarCampos() {
        binding.nombre.setText("")
        binding.procedencia.setText("")
        binding.celular.setText("")
        binding.inputEmail.setText("")
        binding.carrera1.setSelection(0)
        binding.carrera2.setSelection(0)
    }

    private fun validarCarreras(): Boolean {
        return binding.carrera1.selectedItemPosition > 0 && binding.carrera2.selectedItemPosition > 0
    }
}