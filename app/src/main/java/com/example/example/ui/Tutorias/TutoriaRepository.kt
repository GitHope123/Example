package com.example.example.ui.Tutorias

import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TutoriaRepository {

    private val firestore = FirebaseFirestore.getInstance()


    fun getIncidenciasPorGradoSeccion(
        grado: Int,
        seccion: String,
        callback: (List<TutoriaClass>) -> Unit
    ) {
        var query = firestore.collection("Incidencia")
            .whereEqualTo("grado", grado)
            .whereEqualTo("seccion", seccion)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val incidencias = querySnapshot.documents.mapNotNull { document ->
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
                        urlImagen = document.getString("urlImagen") ?: "",
                        cargo= document.getString("cargo") ?: "",
                    )
                }
                callback(incidencias)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(emptyList())
            }
/*
                val filteredList = when (filtroFecha) {
                    "Hoy" -> filterByToday(incidencias)
                    "Ultimos 7 dias" -> filterByLast7Days(incidencias)
                    "Este mes" -> filterByCurrentMonth(incidencias)
                    "Este año" -> filterByCurrentYear(incidencias)
                    else -> incidencias // No se aplica filtro de fecha
                }

                callback(filteredList)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(emptyList())
            }
            */
    }
/*
    private fun filterByToday(incidencias: List<TutoriaClass>): List<TutoriaClass> {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return incidencias.filter {
            val date = parseDate(it.fecha, it.hora)
            date != null && date.after(today) && date.before(Date())
        }
    }

    private fun filterByLast7Days(incidencias: List<TutoriaClass>): List<TutoriaClass> {
        val last7Days = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }.time

        return incidencias.filter {
            val date = parseDate(it.fecha, it.hora)
            date != null && date.after(last7Days) && !date.after(Date())
        }
    }

    private fun filterByCurrentMonth(incidencias: List<TutoriaClass>): List<TutoriaClass> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        return incidencias.filter {
            val date = parseDate(it.fecha, it.hora)
            date != null && calendar.apply { time = date }.let {
                it.get(Calendar.MONTH) == currentMonth && it.get(Calendar.YEAR) == currentYear
            }
        }
    }

    private fun filterByCurrentYear(incidencias: List<TutoriaClass>): List<TutoriaClass> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        return incidencias.filter {
            val date = parseDate(it.fecha, it.hora)
            date != null && Calendar.getInstance().apply { time = date }.get(Calendar.YEAR) == currentYear
        }
    }

    private fun parseDate(fecha: String, hora: String): Date? {
        return try {
            dateTimeFormat.parse("$fecha $hora")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }*/
}
