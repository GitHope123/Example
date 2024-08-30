package com.example.example.ui.profesores

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
import com.example.example.ui.tutores.TutorFragment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ProfesorFragment : Fragment() {

    private var _binding: FragmentProfesorBinding? = null
    private val binding get() = _binding!!
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val profesorList = mutableListOf<Profesor>()
    private val filteredProfesorList = mutableListOf<Profesor>()
    private lateinit var profesorAdapter: ProfesorAdapter
    private var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userType = it.getString("USER_TYPE")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfesorBinding.inflate(inflater, container, false)
        setupRecyclerView()
        fetchProfesores()
        setupSearchView()
        setupButtons()
        return binding.root
    }

    private fun setupRecyclerView() {
        val isEditButtonVisible = when (userType) {
            "Administrador" -> View.VISIBLE
            else -> View.INVISIBLE
        }

        profesorAdapter = ProfesorAdapter(
            profesores = filteredProfesorList,
            onEditClickListener = { profesor ->
                Toast.makeText(context, "Edit button clicked for ${profesor.nombres}", Toast.LENGTH_SHORT).show()
            },
            isEditButtonVisible = isEditButtonVisible // Pasa la visibilidad del editButton
        )

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


    private fun setupButtons() {
        binding.addButtomProfesor.setOnClickListener {
            startActivity(Intent(context, AddProfesor::class.java))
        }

        val isAddButtonVisible = when (userType) {
            "administrador"-> View.VISIBLE
            else -> View.VISIBLE
        }
        binding.addButtomProfesor.visibility = isAddButtonVisible

        // Determina la visibilidad del editButton
        val isEditButtonVisible = when (userType) {
            "administrador" -> View.VISIBLE
            else -> View.VISIBLE
        }

        // Asegúrate de que la lista de profesores está disponible
        profesorAdapter = ProfesorAdapter(
            profesores = filteredProfesorList,
            onEditClickListener = { profesor ->
                Toast.makeText(context, "Edit button clicked for ${profesor.nombres}", Toast.LENGTH_SHORT).show()
            },
            isEditButtonVisible = isEditButtonVisible // Pasa la visibilidad del editButton
        )

        binding.recyclerViewProfesores.adapter = profesorAdapter
    }

    private fun DocumentSnapshot.toProfesor(): Profesor? {
        return try {
            Profesor(
                idProfesor = id,
                nombres = getString("nombres") ?: "",
                apellidos = getString("apellidos") ?: "",
                celular = getLong("celular") ?: 0L,
                materia = getString("materia") ?: "",
                correo = getString("correo") ?: "",
                grado = getLong("grado") ?: 0L,
                seccion = getString("seccion") ?: "",
                password = getString("password")?:""

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

    fun refreshData() {
        fetchProfesores()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    override fun onPause() {
        super.onPause()
        clearSearchView()
    }

    private fun clearSearchView() {
        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
    }

    companion object {
        @JvmStatic
        fun newInstance(userType: String) =
            ProfesorFragment().apply {
                arguments = Bundle().apply {
                    putString("USER_TYPE", userType)
                }
            }
    }
}
