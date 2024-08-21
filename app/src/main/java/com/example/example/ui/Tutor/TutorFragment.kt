package com.example.example.ui.Tutor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView // Asegúrate de importar la clase correcta
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.Profesor.Profesor
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class TutorFragment : Fragment() {

    private val ADD_TUTOR_REQUEST_CODE = 1
    private lateinit var tutorAdapter: TutorAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerViewTutores: RecyclerView
    private lateinit var searchViewTutor: SearchView
    private lateinit var addButtonTutor: FloatingActionButton
    private var originalList: List<Profesor> = emptyList() // Initialize with an empty list

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
        }, isButtonVisible = false, istextViewGradosSeccionVisible = true)
        recyclerViewTutores.adapter = tutorAdapter

        fetchProfesores()

        addButtonTutor = view.findViewById(R.id.addButtonTutor)
        searchViewTutor = view.findViewById(R.id.searchViewTutor) as SearchView

        searchViewTutor.setOnClickListener {
            searchViewTutor.isIconified = false
            searchViewTutor.requestFocus()
        }

        searchViewTutor.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { handleSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { handleSearch(it) }
                return true
            }
        })

        addButtonTutor.setOnClickListener {
            val intent = Intent(requireContext(), AddTutor::class.java)
            startActivityForResult(intent, ADD_TUTOR_REQUEST_CODE)
        }
    }

    private fun handleSearch(query: String) {
        if (originalList.isEmpty()) {
            return
        }
        val filteredList = originalList.filter {
            it.nombres.contains(query, ignoreCase = true)
        }
        tutorAdapter.updateList(filteredList)
    }

    private fun fetchProfesores() {
        db.collection("Profesor")
            .whereEqualTo("tutor", true)
            .get()
            .addOnSuccessListener { result ->
                originalList = result.documents.mapNotNull { document ->
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
                        null
                    }
                }
                tutorAdapter.updateList(originalList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching tutors: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_TUTOR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            fetchProfesores()
        }
    }
}

