package com.example.example.ui.Incidencia.Estado

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.Incidencia.EstudianteAgregar
import com.google.firebase.firestore.FirebaseFirestore

class Todo : Fragment() {

    private lateinit var recyclerViewIncidencia: RecyclerView
    private lateinit var adapter: IncidenciaAdapter
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var incidencias: MutableList<IncidenciaClass> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_todo, container, false)
        init(view)
        return view
    }
        private fun init(view: View){
            recyclerViewIncidencia = view.findViewById(R.id.recyclerViewIncidencia)
            recyclerViewIncidencia.layoutManager = LinearLayoutManager(context)
            adapter = IncidenciaAdapter(incidencias, requireContext())
            recyclerViewIncidencia.adapter = adapter
            loadAllIncidencias()
        }
    private fun loadAllIncidencias() {
        firestore.collection("Incidencia").get().addOnSuccessListener { result ->
                incidencias.clear()
                for (document in result) {
                    val id = document.id  // Obtener el ID del documento
                    val fecha = document.getString("fecha") ?: ""
                    val hora = document.getString("hora") ?: ""
                    val nombreEstudiante = document.getString("nombreEstudiante") ?: ""
                    val apellidoEstudiante = document.getString("apellidoEstudiante") ?: ""
                    val tipo = document.getString("tipo") ?: ""
                    val gravedad = document.getString("gravedad") ?: ""
                    val estado = document.getString("estado") ?: ""
                    val detalle = document.getString("detalle") ?: ""
                    incidencias.add(IncidenciaClass( id, fecha, hora, nombreEstudiante, apellidoEstudiante, tipo, gravedad, estado, detalle))
                }
                adapter.updateData(incidencias)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

}
