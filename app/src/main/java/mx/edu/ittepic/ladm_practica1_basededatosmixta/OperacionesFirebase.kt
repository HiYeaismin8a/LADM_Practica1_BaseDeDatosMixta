package mx.edu.ittepic.ladm_practica1_basededatosmixta

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OperacionesFirebase() {
    private val db = Firebase.firestore
    fun getPostulante(id: String): Task<DocumentSnapshot> {
        return db.collection("postulantes").document(id).get()
    }

    fun getPostulantes(): Task<QuerySnapshot> {
        return db.collection("postulantes").get()
    }

    fun agregarPostulante(postulante: Postulante): Task<DocumentReference> {
        return db.collection("postulantes").add(postulante.mapear())
    }

    fun agregarPostulantes(postulantes: ArrayList<Postulante>): Task<Void> {
        val batch: WriteBatch = db.batch()

        postulantes.forEach {
            batch.set(db.collection("postulantes").document(), it.mapear())

        }
        return batch.commit()
    }

    fun actualizarPostulante(postulante: Postulante): Task<Void> {
        return db.collection("postulantes")
            .document(postulante.id!!)
            .update(postulante.mapear() as Map<String, Any>)
    }

    fun eliminarPostulante(postulante: Postulante): Task<Void> {
        return db.collection("postulantes").document(postulante.id!!).delete()
    }
}