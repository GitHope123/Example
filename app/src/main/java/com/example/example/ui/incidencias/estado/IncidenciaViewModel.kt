package com.example.example.ui.incidencias.estado

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class IncidenciaViewModel : ViewModel() {

    private val _incidenciasLiveData = MutableLiveData<List<IncidenciaClass>>()
    private val _incidenciasFiltradasLiveData = MutableLiveData<List<IncidenciaClass>>()
    val incidenciasLiveData: LiveData<List<IncidenciaClass>> get() = _incidenciasLiveData
    val incidenciasFiltradasLiveData: LiveData<List<IncidenciaClass>> get() = _incidenciasFiltradasLiveData

    // Cargar incidencias desde el repositorio y actualizar LiveData
    fun cargarIncidencias(idProfesor: String, repositorio: IncidenciaRepository) {
        repositorio.getIncidenciaByEstado(idProfesor) { incidencias ->
            _incidenciasLiveData.value = incidencias
            _incidenciasFiltradasLiveData.value = incidencias.sortedByDescending {
                parseDateTime(it.fecha, it.hora)
            }
        }
    }

    fun filtrarIncidenciasPorEstado(estado: String) {
        _incidenciasLiveData.value?.let { incidencias ->
            val incidenciasFiltradas = if (estado.isEmpty()) {
                // Si no hay estado, mostrar todas
                incidencias
            } else {
                // Filtrar por estado
                incidencias.filter { it.estado == estado }
            }

            // Ordenar las incidencias filtradas por fecha y hora
            _incidenciasFiltradasLiveData.value = incidenciasFiltradas.sortedByDescending {
                parseDateTime(it.fecha, it.hora)
            }
        }
    }

    private fun parseDateTime(fecha: String, hora: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
        return try {
            LocalDateTime.parse("$fecha $hora", formatter)
        } catch (e: DateTimeParseException) {
            LocalDateTime.MIN
        }
    }
}