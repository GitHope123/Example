package com.example.example.ui.Tutor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.Profesor.Profesor
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class TutorFragment : Fragment() {

    private val ADD_TUTOR_REQUEST_CODE = 1 // Define a request code
    private lateinit var tutorAdapter: TutorAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerViewTutores: RecyclerView
    private lateinit var addButtonTutor: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tutor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewTutores = view.findViewById(R.id.recyclerViewTutores)
        recyclerViewTutores.layoutManager = LinearLayoutManager(requireContext())

        tutorAdapter = TutorAdapter(emptyList(), { profesor ->
            Toast.makeText(requireContext(), "Selected: ${profesor.nombres}", Toast.LENGTH_SHORT).show()
        }, isButtonVisible = false)
        recyclerViewTutores.adapter = tutorAdapter

        fetchProfesores()

        addButtonTutor = view.findViewById(R.id.addButtonTutor)
        addButtonTutor.setOnClickListener {
            // Start AddTutor activity for result
            val intent = Intent(requireContext(), AddTutor::class.java)
            startActivityForResult(intent, ADD_TUTOR_REQUEST_CODE)
        }
    }

    private fun fetchProfesores() {
        db.collection("Profesor")
            .whereEqualTo("tutor", true)
            .get()
            .addOnSuccessListener { result ->
                val listaProfesores = result.documents.mapNotNull { document ->
                    try {
                        val profesor = document.toObject(Profesor::class.java)?.apply {
                            idProfesor = document.id
                            nombres = document.getString("nombres") ?: ""
                            apellidos = document.getString("apellidos") ?: ""
                            celular = document.getLong("celular") ?: 0
                            materia = document.getString("materia") ?: ""
                            correo = document.getString("correo") ?: ""
                            tutor = document.getBoolean("tutor") ?: false
                            grado = when (val gradoValue = document.get("grado")) {
                                is Long -> gradoValue
                                is String -> gradoValue.toLongOrNull() ?: 0
                                else -> 0
                            }
                            seccion = document.getString("seccion") ?: ""
                        }
                        profesor
                    } catch (e: Exception) {
                        // Maneja el error de forma apropiada si ocurre una excepción
                        null
                    }
                }
                tutorAdapter.updateList(listaProfesores)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching tutors: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_TUTOR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Refresh the data
            fetchProfesores()
        }
    }
}
