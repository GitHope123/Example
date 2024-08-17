package com.example.example.ui.Estudiante

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.databinding.FragmentEstudianteBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import androidx.appcompat.widget.SearchView
import com.example.example.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GalleryFragment : Fragment() {

    private var _binding: FragmentEstudianteBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var studentAdapter: StudentAdapter
    private val estudiantes = mutableListOf<Estudiante>()
    private val fullEstudiantesList = mutableListOf<Estudiante>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEstudianteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        setupRecyclerView()
        setupSearchView()
        setupSpinner()
        fetchEstudiantes()
        addStudents()
        editStudents()
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(estudiantes)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = studentAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Not used but can be implemented if needed
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the list based on the updated query text
                filterEstudiantes(newText)
                return true
            }
        })
    }

    private fun setupSpinner() {
    }


    private fun fetchEstudiantes() {
        showProgressBar(true) // Show ProgressBar before fetching data

        firestore.collection("Estudiante")
            .orderBy("estudiante", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                showProgressBar(false) // Hide ProgressBar when data is fetched

                if (e != null) {
                    // Handle the error (e.g., show a message to the user or log the error)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val newEstudiantes = it.documents.mapNotNull { document ->
                        document.toObject(Estudiante::class.java)
                    }
                    estudiantes.clear()
                    estudiantes.addAll(newEstudiantes)
                    fullEstudiantesList.clear()
                    fullEstudiantesList.addAll(newEstudiantes)
                    studentAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun filterEstudiantes(query: String?) {
        val queryLower =
            query?.lowercase().orEmpty() // Convert query to lowercase for case-insensitive matching
        val filteredEstudiantes = if (queryLower.isEmpty()) {
            fullEstudiantesList // Use the full list if the query is empty
        } else {
            fullEstudiantesList.filter { estudiante ->
                estudiante.estudiante.lowercase().contains(queryLower)
            }
        }
        estudiantes.clear()
        estudiantes.addAll(filteredEstudiantes)
        studentAdapter.notifyDataSetChanged()
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addStudents() {
        view?.findViewById<FloatingActionButton>(R.id.addButtom)?.let { addButton ->
            addButton.setOnClickListener {
                val intent = Intent(requireContext(), AddEstudiante::class.java)
                startActivity(intent)
            }
        } ?: run {

        }
    }

    private fun editStudents() {
        view?.findViewById<ImageButton>(R.id.editStudents)?.let { buttonEditStudents ->
            buttonEditStudents.setOnClickListener {
                val intent = Intent(requireContext(), EditEstudiante::class.java)
                startActivity(intent)
            }
        } ?: run {
        }
    }


}
