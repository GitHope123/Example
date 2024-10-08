package com.example.example.ui.profesores

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.InicioSesion
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
    private lateinit var userType: String
    private var visibilityUser:Boolean=false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfesorBinding.inflate(inflater, container, false)
        userType= InicioSesion.GlobalData.datoTipoUsuario
        if(userType=="Administrador"){
            visibilityUser=true
        }
        setupRecyclerView()
        fetchProfesores()
        setupSearchView()
        setupButtons()
        return binding.root
    }

    private fun setupRecyclerView() {
        profesorAdapter = ProfesorAdapter(
            context = requireContext(),
            profesores = filteredProfesorList,
            onEditClickListener = { profesor -> },
            isEditButtonVisible = visibilityUser
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
            "Administrador" -> View.VISIBLE
            else -> View.GONE
        }
        binding.addButtomProfesor.visibility = isAddButtonVisible
    }

    private fun DocumentSnapshot.toProfesor(): Profesor? {
        return try {
            Profesor(
                idProfesor = id,
                nombres = getString("nombres") ?: "",
                apellidos = getString("apellidos") ?: "",
                celular = getLong("celular") ?: 0L,
                cargo = getString("cargo") ?: "",
                correo = getString("correo") ?: "",
                grado = getLong("grado") ?: 0L,
                seccion = getString("seccion") ?: "",
                password = getString("password") ?: "",
                dni = getLong("dni") ?: 0L
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

    // Manejo de permisos
    fun requestCallPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CALL_PHONE)) {
            AlertDialog.Builder(requireContext())
                .setTitle("Permiso de llamada")
                .setMessage("Esta aplicación necesita acceso para realizar llamadas.")
                .setPositiveButton("Aceptar") { _, _ ->
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_REQUEST_CODE)
                }
                .setNegativeButton("Cancelar", null)
                .create()
                .show()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
            } else {
                Toast.makeText(context, "Permiso de llamada denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}
