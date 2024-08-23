package com.example.example.ui.incidencias.estado

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.google.firebase.firestore.FirebaseFirestore

class Pendiente : Fragment() {
    private lateinit var recyclerViewIncidencia: RecyclerView
    private lateinit var adapter: IncidenciaAdapter
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var incidenciasPendiente: MutableList<IncidenciaClass> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pendiente, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        recyclerViewIncidencia = view.findViewById(R.id.recyclerViewIncidencia)
        recyclerViewIncidencia.layoutManager = LinearLayoutManager(context)
        adapter = IncidenciaAdapter(incidenciasPendiente, requireContext())
        recyclerViewIncidencia.adapter = adapter
        loadAllIncidencias()
    }
    private fun loadAllIncidencias() {
        firestore.collection("Incidencia").get().addOnSuccessListener { result ->
            incidenciasPendiente.clear()
            for (document in result) {
                val estado = document.getString("estado") ?: ""
                if (estado == "Pendiente") {
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

                    incidenciasPendiente.add(IncidenciaClass(
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
                    ))
                }
            }
            adapter.updateData(incidenciasPendiente)
        }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }
    override fun onResume() {
        super.onResume()
        loadAllIncidencias()  // Recargar los datos cuando el fragmento se reanuda
    }

}
