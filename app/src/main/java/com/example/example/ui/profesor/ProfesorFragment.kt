package com.example.example.ui.profesor
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
    private val filteredProfesorList = mutableListOf<Profesor>()
    private lateinit var profesorAdapter: ProfesorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfesorBinding.inflate(inflater, container, false)
        setupRecyclerView()
        fetchProfesores()
        setupSearchView()
        binding.addButtomProfesor.setOnClickListener {
            startActivity(Intent(context, AddProfesor::class.java))
        }
        return binding.root
    }

    private fun setupRecyclerView() {
        profesorAdapter = ProfesorAdapter(filteredProfesorList) {
            // Handle edit action for the professor here if needed
        }
        binding.recyclerViewProfesores.apply {
            adapter = profesorAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
            binding.searchView.requestFocus()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterProfesores(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterProfesores(it) }
                return true
            }
        })
        
    }

    private fun filterProfesores(query: String) {
        val filteredList = profesorList.filter {
            it.nombres.contains(query, ignoreCase = true) ||
                    it.apellidos.contains(query, ignoreCase = true) ||
                    it.celular.toString().contains(query, ignoreCase = true) ||
                    it.correo.contains(query, ignoreCase = true)
        }
        filteredProfesorList.clear()
        filteredProfesorList.addAll(filteredList)
        profesorAdapter.notifyDataSetChanged()
    }


    private fun fetchProfesores() {
        firestore.collection("Profesor")
            .get()
            .addOnSuccessListener { result ->
                profesorList.clear()
                result.documents.forEach { document ->
                    document.toProfesor()?.let {
                        profesorList.add(it)
                    }
                }
                filteredProfesorList.clear()
                filteredProfesorList.addAll(profesorList)
                profesorAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("ProfesorFragment", "Error fetching profesores", exception)
                Toast.makeText(context, "Error al cargar los profesores: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    fun refreshData() {
        fetchProfesores()
    }

    private fun DocumentSnapshot.toProfesor(): Profesor? {
        return try {
            Profesor(
                idProfesor = id, // Use the document ID as idProfesor
                nombres = getString("nombres") ?: "",
                apellidos = getString("apellidos") ?: "",
                celular = getLong("celular") ?: 0L, // Directly get Long value
                materia = getString("materia") ?: "",
                correo = getString("correo") ?: "",
                grado = getLong("grado") ?: 0L,
                seccion = getString("seccion")?: ""
            )
        } catch (e: Exception) {
            Log.e("ProfesorFragment", "Error converting document to Profesor", e)
            null
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        refreshData() // Refresh data when the fragment becomes visible
    }
    override fun onPause() {
        super.onPause()
        clearSearchView() // Limpiar el SearchView al pausar el fragmento
    }

    private fun clearSearchView() {
        binding.searchView.setQuery("", false) // Limpiar el texto sin activar la búsqueda nuevamente
        binding.searchView.clearFocus() // Opcional: para eliminar el foco del SearchView
    }
}
