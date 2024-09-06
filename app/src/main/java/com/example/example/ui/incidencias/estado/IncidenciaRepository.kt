package com.example.example.ui.incidencias.estado
import com.google.firebase.firestore.FirebaseFirestore

class IncidenciaRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getIncidenciaByEstado(
        idProfesor: String,
        estado: String,
        onComplete: (List<IncidenciaClass>) -> Unit
    ) {
        var query = firestore.collection("Incidencia")
            .whereEqualTo("idProfesor", idProfesor)

        if (estado.isNotEmpty()) {
            query = query.whereEqualTo("estado", estado)
        }
            query.get()
            .addOnSuccessListener { querySnapshot ->
                val incidencias = querySnapshot.documents.mapNotNull { document ->
                    IncidenciaClass(
                        id = document.id,
                        fecha = document.getString("fecha") ?: "",
                        hora = document.getString("hora") ?: "",
                        nombreEstudiante = document.getString("nombreEstudiante") ?: "",
                        apellidoEstudiante = document.getString("apellidoEstudiante") ?: "",
                        grado = document.getLong("grado")?.toInt() ?: 0,
                        seccion = document.getString("seccion") ?: "",
                        tipo = document.getString("tipo") ?: "",
                        gravedad = document.getString("gravedad") ?: "",
                        estado = document.getString("estado") ?: "",
                        detalle = document.getString("detalle") ?: "",
                        imageUri = document.getString("urlImagen") ?: ""
                    )
                }
                onComplete(incidencias)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                onComplete(emptyList())  // Retorna lista vacía si falla
            }
    }
}
