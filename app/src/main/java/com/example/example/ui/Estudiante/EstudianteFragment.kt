package com.example.example.ui.Estudiante

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.databinding.FragmentEstudianteBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import androidx.appcompat.widget.SearchView

class EstudianteFragment : Fragment() {

    private var _binding: FragmentEstudianteBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var studentAdapter: StudentAdapter
    private val estudiantes = mutableListOf<Estudiante>()
    private val fullEstudiantesList = mutableListOf<Estudiante>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentEstudianteBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        setupRecyclerView()
        setupSearchView()
        fetchEstudiantes()
        setupButtons()
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(estudiantes) { estudiante ->
            // Maneja la edición del estudiante aquí
            val intent = Intent(requireContext(), EditEstudiante::class.java).apply {
                putExtra("ESTUDIANTE_ID", estudiante.id) // Pasa el ID o cualquier dato necesario para editar
            }
            startActivity(intent)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?) = filterEstudiantes(newText).let { true }
        })
    }

    private fun fetchEstudiantes() {
        showProgressBar(true)

        firestore.collection("Aula")
            .addSnapshotListener { snapshot, e ->
                showProgressBar(false)

                if (e != null) {
                    // Handle the error (e.g., show a message or log the error)
                    return@addSnapshotListener
                }

                snapshot?.documents?.let { documents ->
                    val newEstudiantes = mutableListOf<Estudiante>()
                    documents.forEach { document ->
                        val estudiantesList = document.get("estudiantes") as? List<Map<String, Any>> ?: emptyList()
                        estudiantesList.mapNotNull { it.toEstudiante() }.let {
                            newEstudiantes.addAll(it)
                        }
                    }
                    estudiantes.clear()
                    estudiantes.addAll(newEstudiantes)
                    fullEstudiantesList.clear()
                    fullEstudiantesList.addAll(newEstudiantes)
                    studentAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun Map<String, Any>.toEstudiante(): Estudiante? {
        return try {
            Estudiante(
                id = this["id"] as? String ?: "",
                apellidos = this["apellidos"] as? String ?: "",
                celularApoderado = (this["celularApoderado"] as? Number)?.toLong() ?: 0,
                dni = (this["dni"] as? Number)?.toLong() ?: 0,
                grado = (this["grado"] as? Number)?.toInt() ?: 0,
                nombres = this["nombres"] as? String ?: "",
                seccion = this["seccion"] as? String ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun filterEstudiantes(query: String?) {
        val queryLower = query?.lowercase().orEmpty()
        estudiantes.clear()
        estudiantes.addAll(
            if (queryLower.isEmpty()) {
                fullEstudiantesList
            } else {
                fullEstudiantesList.filter {
                    it.nombres.lowercase().contains(queryLower) ||
                            it.apellidos.lowercase().contains(queryLower)
                }
            }
        )
        studentAdapter.notifyDataSetChanged()
    }

    private fun showProgressBar(show: Boolean) {
        // Verificar que binding no sea null antes de usarlo
        if (_binding != null) {
            binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    private fun setupButtons() {
        binding.addButtom.setOnClickListener {
            startActivity(Intent(requireContext(), AddEstudiante::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
