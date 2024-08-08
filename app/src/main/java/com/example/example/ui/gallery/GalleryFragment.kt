package com.example.example.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.databinding.FragmentGalleryBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QuerySnapshot
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var studentAdapter: StudentAdapter
    private val estudiantes = mutableListOf<Estudiante>()
    private var lastVisible: DocumentSnapshot? = null
    private val pageSize = 20
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firestore = FirebaseFirestore.getInstance()
        studentAdapter = StudentAdapter(estudiantes)
        binding.recyclerView.apply {
            adapter = studentAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(createScrollListener())
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchStudents(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchStudents(it) }
                return true
            }
        })

        loadStudents()

        return root
    }

    private fun createScrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                isLoading = true
                loadMoreStudents()
            }
        }
    }

    private fun loadStudents() {
        firestore.collection("Estudiante")
            .limit(pageSize.toLong())
            .get()
            .addOnSuccessListener { result ->
                handleStudentLoadResult(result)
            }
            .addOnFailureListener { exception ->
                handleLoadError(exception)
            }
    }

    private fun loadMoreStudents() {
        lastVisible?.let { lastDoc ->
            firestore.collection("Estudiante")
                .startAfter(lastDoc)
                .limit(pageSize.toLong())
                .get()
                .addOnSuccessListener { result ->
                    handleStudentLoadResult(result)
                }
                .addOnFailureListener { exception ->
                    handleLoadError(exception)
                }
        }
    }

    private fun searchStudents(query: String) {
        firestore.collection("Estudiante")
            .whereGreaterThanOrEqualTo("estudiante", query)
            .whereLessThanOrEqualTo("estudiante", query + '\uf8ff')
            .get()
            .addOnSuccessListener { result ->
                handleStudentLoadResult(result)
            }
            .addOnFailureListener { exception ->
                handleLoadError(exception)
            }
    }

    private fun handleStudentLoadResult(result: QuerySnapshot) {
        val newEstudiantes = result.documents.mapNotNull { document ->
            document.toObject(Estudiante::class.java)
        }

        estudiantes.clear()
        estudiantes.addAll(newEstudiantes)
        studentAdapter.notifyDataSetChanged()

        if (result.documents.isNotEmpty()) {
            lastVisible = result.documents.last()
        }
        isLoading = false
    }

    private fun handleLoadError(exception: Exception) {
        // Manejo de errores, por ejemplo, mostrando un mensaje al usuario
        // Toast.makeText(context, "Error al cargar datos: $exception", Toast.LENGTH_SHORT).show()
        println("Error getting documents: $exception")
        isLoading = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



