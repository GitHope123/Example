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
import com.google.firebase.firestore.FirebaseFirestore

class ProfesorFragment : Fragment() {

    private var _binding: FragmentProfesorBinding? = null
    private val binding get() = _binding!!

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var profesorAdapter: ProfesorAdapter
    private val profesorList = mutableListOf<Profesor>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfesorBinding.inflate(inflater, container, false)

        // Initialize RecyclerView and Adapter
        profesorAdapter = ProfesorAdapter(profesorList)
        binding.recyclerViewProfesores.apply {
            adapter = profesorAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Fetch data from Firestore
        fetchProfesores()

        // Set up FloatingActionButton click listener
        binding.addButtomProfesor.setOnClickListener {
            val intent = Intent(activity, addProfesor::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun fetchProfesores() {
        firestore.collection("Docente")
            .get()
            .addOnSuccessListener { result ->
                profesorList.clear() // Clear existing data
                for (document in result) {
                    val data = document.data // Get the map of data
                    val profesor = data.mapToProfesor() // Convert the map to a Profesor object

                    if (profesor != null) { // Only add the profesor if it's valid
                        profesorList.add(profesor)
                    }
                }
                profesorAdapter.notifyDataSetChanged() // Notify adapter that data has changed
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al cargar los profesores: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    // Extension function to convert a Map<String, Any> to a Profesor object
    private fun Map<String, Any>.mapToProfesor(): Profesor? {
        return try {
            val apellidos = this["apellidos"] as? String ?: return null
            val nombres = this["nombres"] as? String ?: return null
            val domicilio = this["domicilio"] as? String ?: return null
            val celular = this["celular"] as? String ?: return null
            val materia = this["materia"] as? String ?: return null
            val seccion = this["seccion"] as? String ?: return null
            val grado = this["grado"] as? String ?: return null

            Profesor(
                apellidos = apellidos,
                nombres = nombres,
                domicilio = domicilio,
                celular = celular,
                materia = materia,
                seccion = seccion,
                grado = grado
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null if there's an error during mapping
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
