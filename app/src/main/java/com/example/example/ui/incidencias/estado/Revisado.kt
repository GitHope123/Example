package com.example.example.ui.incidencias.estado

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.BarraLateral
import com.example.example.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Revisado : Fragment() {
    private lateinit var recyclerViewIncidencia: RecyclerView
    private lateinit var incidenciaAdapter: IncidenciaAdapter
    private lateinit var searchView: SearchView
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var incidenciasRevisado: MutableList<IncidenciaClass> = mutableListOf()
    private var incidenciasFilter: MutableList<IncidenciaClass> = mutableListOf()
    private lateinit var idUsuario:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_revisado, container, false)
        init(view)
        setupSearchView()
        return view
    }

    private fun init(view: View) {
        recyclerViewIncidencia = view.findViewById(R.id.recyclerViewIncidencia)
        recyclerViewIncidencia.layoutManager = LinearLayoutManager(context)
        searchView = view.findViewById(R.id.searchView)
        incidenciaAdapter = IncidenciaAdapter(incidenciasRevisado, requireContext())
        recyclerViewIncidencia.adapter = incidenciaAdapter
        loadAllIncidencias()
    }
    private fun loadAllIncidencias() {
        idUsuario = BarraLateral.GlobalData.idUsuario
        firestore.collection("Incidencia").get().addOnSuccessListener { result ->
            incidenciasRevisado.clear()
            for (document in result) {
                val idProfesor = document.getString("idProfesor") ?: ""
                val estado = document.getString("estado") ?: ""
                if (estado == "Revisado") {
                    if(idProfesor == idUsuario) {
                        val id = document.id  // Obtener el ID del documento
                        val fecha = document.getString("fecha") ?: ""
                        val hora = document.getString("hora") ?: ""
                        val nombreEstudiante = document.getString("nombreEstudiante") ?: ""
                        val apellidoEstudiante = document.getString("apellidoEstudiante") ?: ""
                        val tipo = document.getString("tipo") ?: ""
                        val gravedad = document.getString("gravedad") ?: ""
                        val grado = document.getLong("grado")?.toInt() ?: 0
                        val seccion = document.getString("seccion") ?: ""
                        val detalle = document.getString("detalle") ?: ""
                        val urlImagen = document.getString("urlImagen") ?: ""

                        incidenciasRevisado.add(
                            IncidenciaClass(
                                id = id,
                                fecha = fecha,
                                hora = hora,
                                nombreEstudiante = nombreEstudiante,
                                apellidoEstudiante = apellidoEstudiante,
                                grado = grado,
                                seccion = seccion,
                                tipo = tipo,
                                gravedad = gravedad,
                                estado = estado,
                                detalle = detalle,
                                imageUri = urlImagen
                            )
                        )
                    } }
            }
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            incidenciasRevisado.sortByDescending {
                try {
                    dateTimeFormat.parse("${it.fecha} ${it.hora}") ?: Date(0)
                } catch (e: Exception) {
                    Date(0)  // Fecha por defecto si ocurre un error
                }
            }
            incidenciaAdapter.updateData(incidenciasRevisado)
        }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
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
                incidenciasRevisado.filter { incidencia ->
                    val nombreCompleto =
                        "${incidencia.nombreEstudiante} ${incidencia.apellidoEstudiante}".lowercase()
                    queryWords.all { nombreCompleto.contains(it) }
                }
            )
        } else {
            incidenciasFilter.addAll(incidenciasRevisado)  // Mostrar todos los elementos si la búsqueda está vacía
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