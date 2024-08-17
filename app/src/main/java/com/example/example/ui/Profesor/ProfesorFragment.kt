package com.example.example.ui.Profesor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.databinding.FragmentProfesorBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ProfesorFragment : Fragment() {

    private var _binding: FragmentProfesorBinding? = null
    private val binding get() = _binding!!

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val profesorList = mutableListOf<Profesor>()
    private lateinit var profesorAdapter: ProfesorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfesorBinding.inflate(inflater, container, false)

        setupRecyclerView()
        fetchProfesores()

        binding.addButtomProfesor.setOnClickListener {
            startActivity(Intent(context, AddProfesor::class.java))
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        profesorAdapter = ProfesorAdapter(profesorList) { profesor ->
            // Handle edit action for the professor here if needed
        }
        binding.recyclerViewProfesores.apply {
            adapter = profesorAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun fetchProfesores() {
        firestore.collection("Profesor")
            .get()
            .addOnSuccessListener { result ->
                profesorList.clear()
                result.documents.mapNotNull { document ->
                    document.toProfesor()
                }.also {
                    profesorList.addAll(it)
                }
                profesorAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al cargar los profesores: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun DocumentSnapshot.toProfesor(): Profesor? {
        return try {
            Profesor(
                idProfesor = id, // Use the document ID as the idProfesor
                nombres = getString("nombres") ?: "",
                apellidos = getString("apellidos") ?: "",
                domicilio = getString("domicilio") ?: "",
                celular = getString("celular") ?: "",
                materia = getString("materia") ?: "",
                correo = getString("correo") ?: ""
            )
        } catch (e: Exception) {
            e.printStackTrace() // Print stack trace for debugging
            null // Return null if there's an error during mapping
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
