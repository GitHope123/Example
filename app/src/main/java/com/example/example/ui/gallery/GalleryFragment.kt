package com.example.example.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.databinding.FragmentGalleryBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import androidx.appcompat.widget.SearchView

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var studentAdapter: StudentAdapter
    private val estudiantes = mutableListOf<Estudiante>()
    private val fullEstudiantesList = mutableListOf<Estudiante>() // To keep the original list

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        setupRecyclerView()
        setupSearchView()
        setupSpinner()
        fetchEstudiantes()
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
        // Implement spinner setup if needed
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
        val queryLower = query?.lowercase().orEmpty() // Convert query to lowercase for case-insensitive matching
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
}
