package com.example.example.ui.Tutor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.Profesor.Profesor

class TutorFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TutorAdapter
    private lateinit var addButtonTutor: View // Cambié a View para el botón, actualiza con el tipo real si es necesario
    private val profesores = mutableListOf<Profesor>() // Asegúrate de cargar la lista de profesores

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tutor, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewTutores)
        addButtonTutor = view.findViewById(R.id.addButtonTutor) // Asegúrate de que este ID sea correcto

        recyclerView.layoutManager = LinearLayoutManager(context)

        // Inicializa el adaptador con una lista vacía o la lista de profesores
        adapter = TutorAdapter(profesores) { profesor ->
            // Maneja el clic en el ítem si es necesario
        }
        recyclerView.adapter = adapter

        // Configura el clic en el botón para agregar un tutor
        addButtonTutor.setOnClickListener {
            // Reemplaza con tu actividad o fragmento real para agregar un nuevo tutor
            startActivity(Intent(context, AddTutor::class.java))
        }

        // Aquí puedes cargar la lista de profesores, por ejemplo, desde Firebase
        loadProfesores()

        return view
    }

    private fun loadProfesores() {
        // Ejemplo de carga de datos
        // Aquí podrías usar Firebase Firestore para obtener la lista de profesores y actualizar el adaptador
        // Por ejemplo:
        // firestore.collection("Profesor").get().addOnSuccessListener { documents ->
        //     profesores.clear()
        //     for (document in documents) {
        //         val profesor = document.toObject(Profesor::class.java)
        //         profesores.add(profesor)
        //     }
        //     adapter.notifyDataSetChanged()
        // }
    }
}
