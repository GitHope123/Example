package com.example.example.ui.Tutorias

import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TutoriaRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    // Cargar todas las tutorías
    fun loadAllTutorias(callback: (List<TutoriaClass>) -> Unit) {
        firestore.collection("Incidencia").get()
            .addOnSuccessListener { result ->
                val listaTutorias = result.mapNotNull { document ->
                    try {
                        TutoriaClass(
                            id = document.id,
                            apellidoEstudiante = document.getString("apellidoEstudiante") ?: "",
                            apellidoProfesor = document.getString("apellidoProfesor") ?: "",
                            detalle = document.getString("detalle") ?: "",
                            estado = document.getString("estado") ?: "",
                            fecha = document.getString("fecha") ?: "",
                            grado = document.getLong("grado")?.toInt() ?: 0,
                            gravedad = document.getString("gravedad") ?: "",
                            hora = document.getString("hora") ?: "",
                            nombreEstudiante = document.getString("nombreEstudiante") ?: "",
                            nombreProfesor = document.getString("nombreProfesor") ?: "",
                            seccion = document.getString("seccion") ?: "",
                            tipo = document.getString("tipo") ?: "",
                            urlImagen = document.getString("urlImagen") ?: ""
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }.sortedByDescending { tutoria ->
                    parseDate(tutoria.fecha, tutoria.hora) ?: Date(0)
                }

                callback(listaTutorias)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(emptyList())
            }
    }

    // Obtener grado y sección por correo electrónico del tutor
    fun getGradoSeccionTutorByEmail(email: String, callback: (grado: Int, seccion: String) -> Unit) {
        firestore.collection("Profesor")
            .whereEqualTo("correo", email)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val grado = document.getLong("grado")?.toInt() ?: 0
                    val seccion = document.getString("seccion") ?: ""
                    callback(grado, seccion)
                } else {
                    callback(0, "") // No se encontró el profesor
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(0, "") // Error en la búsqueda
            }
    }

    // Obtener incidencias filtradas por grado, sección y estado
    fun getIncidenciasPorGradoSeccion(grado: Int, seccion: String, estado: String, callback: (List<TutoriaClass>) -> Unit) {
        var query = firestore.collection("Incidencia")
            .whereEqualTo("grado", grado)
            .whereEqualTo("seccion", seccion)

        // Si el estado no está vacío, agregar filtro por estado
        if (estado.isNotEmpty()) {
            query = query.whereEqualTo("estado", estado)
        }

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val incidencias = querySnapshot.documents.mapNotNull { it.toObject(TutoriaClass::class.java) }
                callback(incidencias)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(emptyList()) // Lista vacía en caso de error
            }
    }

    // Función auxiliar para analizar la fecha y hora
    private fun parseDate(fecha: String, hora: String): Date? {
        return try {
            dateTimeFormat.parse("$fecha $hora")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
