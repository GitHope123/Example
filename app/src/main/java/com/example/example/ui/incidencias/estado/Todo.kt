package com.example.example.ui.incidencias.estado

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.InicioSesion
import com.example.example.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Todo : Fragment() {

    private lateinit var recyclerViewIncidencia: RecyclerView
    private lateinit var incidenciaAdapter: IncidenciaAdapter
    private var incidencias: MutableList<IncidenciaClass> = mutableListOf()
    private var incidenciasFilter: MutableList<IncidenciaClass> = mutableListOf()
    private lateinit var searchView: SearchView
    private lateinit var idUsuario:String
    private val incidenciaRepository: IncidenciaRepository = IncidenciaRepository()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todo, container, false)
        init(view)
        setupSearchView()
        return view
    }

    private fun init(view: View) {
        recyclerViewIncidencia = view.findViewById(R.id.recyclerViewIncidencia)
        searchView = view.findViewById(R.id.searchView)
        recyclerViewIncidencia.layoutManager = LinearLayoutManager(context)
        incidenciaAdapter = IncidenciaAdapter(incidencias, requireContext())
        recyclerViewIncidencia.adapter = incidenciaAdapter
        loadAllIncidencias()

    }

    private fun loadAllIncidencias() {
        idUsuario = InicioSesion.GlobalData.idUsuario
        incidenciaRepository.getIncidenciaByEstado(idProfesor = idUsuario, estado = "") { incidenciasList ->
            if (isAdded) {
                incidencias.clear()
                incidencias.addAll(incidenciasList)
                incidencias.sortByDescending {
                    try {
                        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        dateTimeFormat.parse("${it.fecha} ${it.hora}") ?: Date(0)
                    } catch (e: Exception) {
                        Date(0)  // Fecha por defecto si ocurre un error
                    }
                }
                incidenciaAdapter.updateData(incidencias)
            }
        }
    }

    private fun setupSearchView() {
        searchView.setOnClickListener {
            searchView.isIconified = false
            searchView.requestFocus()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterEstudiante(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterEstudiante(it) }
                return true
            }
        })
    }

    private fun filterEstudiante(query: String) {
        val queryWords = query.lowercase().split("\\s+".toRegex())

        incidenciasFilter.clear()

        if (query.isNotEmpty()) {
            incidenciasFilter.addAll(
                incidencias.filter { incidencia ->
                    val nombreCompleto =
                        "${incidencia.nombreEstudiante} ${incidencia.apellidoEstudiante}".lowercase()
                    queryWords.all { nombreCompleto.contains(it) }
                }
            )
        } else {
            incidenciasFilter.addAll(incidencias)  // Mostrar todos los elementos si la búsqueda está vacía
        }

        incidenciaAdapter.updateData(incidenciasFilter)
    }


    override fun onResume() {
        super.onResume()
        loadAllIncidencias()  // Recargar los datos cuando el fragmento se reanuda
        clearSearchView()    // Limpiar el texto de la búsqueda
    }

    private fun clearSearchView() {
        searchView.setQuery("", false)  // Limpiar el texto de búsqueda sin disparar la búsqueda
        searchView.clearFocus()         // Quitar el foco del SearchView
    }

}
