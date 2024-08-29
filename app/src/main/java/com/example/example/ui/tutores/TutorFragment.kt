package com.example.example.ui.tutores

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.profesores.AddProfesor
import com.example.example.ui.profesores.Profesor
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class TutorFragment : Fragment() {

    private val ADD_TUTOR_REQUEST_CODE = 1
    private lateinit var tutorAdapter: TutorAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerViewTutores: RecyclerView
    private lateinit var searchViewTutor: SearchView
    private lateinit var addButtonTutor: FloatingActionButton
    private var originalList: List<Profesor> = emptyList() // List for all the fetched data
    private var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userType = it.getString("USER_TYPE")
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(userType: String) =
            TutorFragment().apply {
                arguments = Bundle().apply {
                    putString("USER_TYPE", userType)
                }
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tutor, container, false)

        // Initialize views here
        recyclerViewTutores = view.findViewById(R.id.recyclerViewTutores)
        searchViewTutor = view.findViewById(R.id.searchViewTutor) as SearchView
        addButtonTutor = view.findViewById(R.id.addButtonTutor)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewTutores.layoutManager = LinearLayoutManager(requireContext())

        tutorAdapter = TutorAdapter(
            onEditClickListener = { profesor ->
                // Implement edit functionality
            },
            onRemoveClickListener = { profesor ->
                // Aquí llamas a Firestore para eliminar el tutor
                profesor.idProfesor?.let { id ->
                    db.collection("Profesor").document(id)
                        .update(
                            "grado", 0,
                            "seccion", "",
                            "tutor", false
                        )
                        .addOnSuccessListener {
                            // Filtra la lista actualizada
                            val updatedList = tutorAdapter.currentList.filter { it.idProfesor != id }
                            tutorAdapter.updateList(updatedList)
                            Toast.makeText(requireContext(), "Tutor removido correctamente", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(requireContext(), "Error updating tutor: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            isButtonVisible = false,
            isTextViewGradosSeccionVisible = true,
            isImageButtonQuitarTutor = true
        )

        recyclerViewTutores.adapter = tutorAdapter

        fetchProfesores()

        // Set up SearchView
        searchViewTutor.setOnClickListener {
            searchViewTutor.isIconified = false
            searchViewTutor.requestFocus()
        }

        searchViewTutor.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                handleSearch(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                handleSearch(newText.orEmpty())
                return true
            }
        })

        // Button to add a new tutor
        addButtonTutor.setOnClickListener {
            val intent = Intent(requireContext(), AddTutor::class.java)
            startActivityForResult(intent, ADD_TUTOR_REQUEST_CODE)
        }

        // Setup buttons visibility
        setupButtons()
    }

    private fun setupButtons() {
        addButtonTutor = view?.findViewById(R.id.addButtonTutor) ?: return
        val textViewGradosSeccion: TextView = view?.findViewById(R.id.textViewGradosSeccionTutor) ?: return
        val imageButtonQuitarTutor: ImageButton = view?.findViewById(R.id.imageButtonQuitarTutor) ?: return


        val isAddButtonVisible = userType == "administrador"
        val isImageButtonQuitarTutorVisible = userType == "administrador"
        val isTextViewGradosSeccionVisible = true

        // Set visibility of buttons based on userType
        addButtonTutor.visibility = if (isAddButtonVisible) View.VISIBLE else View.GONE
        textViewGradosSeccion.visibility = if (isTextViewGradosSeccionVisible) View.VISIBLE else View.GONE
        imageButtonQuitarTutor.visibility = if (isImageButtonQuitarTutorVisible) View.VISIBLE else View.GONE

        // Initialize the adapter with the correct visibility for buttons
        tutorAdapter = TutorAdapter(
            onEditClickListener = { profesor ->
            },
            onRemoveClickListener = { profesor ->
            },
            isButtonVisible = isAddButtonVisible,
            isTextViewGradosSeccionVisible = isTextViewGradosSeccionVisible,
            isImageButtonQuitarTutor = isImageButtonQuitarTutorVisible
        )

        recyclerViewTutores.adapter = tutorAdapter
    }



    private fun handleSearch(query: String) {
        if (query.isBlank()) {
            tutorAdapter.resetList() // Restablece la lista completa si la consulta está vacía
        } else {
            tutorAdapter.filterList(query)
        }
    }

    private fun fetchProfesores() {
        db.collection("Profesor")
            .whereEqualTo("tutor", true)
            .get()
            .addOnSuccessListener { result ->
                val listaProfesores = result.documents.mapNotNull { document ->
                    document.toObject(Profesor::class.java)?.apply {
                        idProfesor = document.id
                        nombres = document.getString("nombres") ?: ""
                        apellidos = document.getString("apellidos") ?: ""
                        celular = document.getLong("celular") ?: 0
                        materia = document.getString("materia") ?: ""
                        correo = document.getString("correo") ?: ""
                        tutor = document.getBoolean("tutor") ?: false
                        grado = document.get("grado")?.toString()?.toLongOrNull() ?: 0L
                        seccion = document.getString("seccion") ?: ""
                    }
                }

                tutorAdapter.updateList(listaProfesores) // Actualiza la lista en el adaptador
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching tutors: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Handle the result of the AddTutor activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_TUTOR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            fetchProfesores() // Refresh the list after adding a new tutor
        }
    }
}
