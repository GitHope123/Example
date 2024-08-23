package com.example.example.ui.Incidencia.Estado

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.google.firebase.firestore.FirebaseFirestore

class Revisado : Fragment() {
    private lateinit var recyclerViewIncidencia: RecyclerView
    private lateinit var adapter: IncidenciaAdapter
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var incidenciasRevisado: MutableList<IncidenciaClass> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_revisado, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        recyclerViewIncidencia = view.findViewById(R.id.recyclerViewIncidencia)
        recyclerViewIncidencia.layoutManager = LinearLayoutManager(context)
        adapter = IncidenciaAdapter(incidenciasRevisado, requireContext())
        recyclerViewIncidencia.adapter = adapter
        loadAllIncidencias()
    }
    private fun loadAllIncidencias() {
        firestore.collection("Incidencia").get().addOnSuccessListener { result ->
            incidenciasRevisado.clear()
            for (document in result) {
                val estado = document.getString("estado") ?: ""
                if (estado == "Revisado") {
                    val id = document.id  // Obtener el ID del documento
                    val fecha = document.getString("fecha") ?: ""
                    val hora = document.getString("hora") ?: ""
                    val nombreEstudiante = document.getString("nombreEstudiante") ?: ""
                    val apellidoEstudiante = document.getString("apellidoEstudiante") ?: ""
                    val tipo = document.getString("tipo") ?: ""
                    val gravedad = document.getString("gravedad") ?: ""
                    val detalle = document.getString("detalle") ?: ""

                    incidenciasRevisado.add(IncidenciaClass(id, fecha, hora, nombreEstudiante, apellidoEstudiante, tipo, gravedad, estado, detalle))
                }
            }
            adapter.updateData(incidenciasRevisado)
        }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }


}