package com.example.example.ui.estudiantes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.InicioSesion
import com.example.example.R
import com.example.example.databinding.FragmentEstudianteBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class EstudianteFragment : Fragment() {
    private var _binding: FragmentEstudianteBinding? = null
    private val binding get() = _binding!!
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private lateinit var estudianteAdapter: EstudianteAdapter
    private val filterEstudiantes = mutableListOf<Estudiante>()
    private val fullEstudiantesList = mutableListOf<Estudiante>()
    private lateinit var userType: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEstudianteBinding.inflate(inflater, container, false)
        userType=InicioSesion.GlobalData.datoTipoUsuario
        updateGrado()
        setupRecyclerView()
        setupSearchView()
        setupButtons()
        return binding.root
    }

    private fun updateGrado() {
        val grados = arrayOf("Selecione", "1", "2", "3", "4", "5")
        val adapterGrados = ArrayAdapter(requireContext(), R.layout.spinner_item_selected, grados)
        adapterGrados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGrado.adapter = adapterGrados

        binding.spinnerGrado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val gradoSeleccionado = binding.spinnerGrado.selectedItem.toString()
                updateSecciones(gradoSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun updateSecciones(gradoSeleccionado: String) {
        val secciones = when (gradoSeleccionado) {
            "Seleccione" -> arrayOf("Seleccione")
            "1" -> arrayOf("Seleccione", "A", "B", "C", "D", "E")
            else -> arrayOf("Seleccione", "A", "B", "C", "D")
        }
        val adapterSecciones = ArrayAdapter(requireContext(), R.layout.spinner_item_selected, secciones)
        adapterSecciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSeccion.adapter = adapterSecciones
        binding.spinnerSeccion.isEnabled = gradoSeleccionado != "Selecione"
        binding.spinnerSeccion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val gradoSeleccionado = binding.spinnerGrado.selectedItem.toString()
                val seccionSeleccionada = binding.spinnerSeccion.selectedItem.toString()
                if (gradoSeleccionado != "Seleccione" && seccionSeleccionada != "Seleccione") {
                    fetchEstudiantes(gradoSeleccionado, seccionSeleccionada)
                } else {
                    filterEstudiantes.clear()
                    estudianteAdapter.notifyDataSetChanged()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun filterEstudiantes(query: String) {
        val gradoSeleccionado = binding.spinnerGrado.selectedItem?.toString()
        val seccionSeleccionada = binding.spinnerSeccion.selectedItem?.toString()
        val queryWords = query.lowercase().split("\\s+".toRegex())
        filterEstudiantes.clear()
        fullEstudiantesList.filterTo(filterEstudiantes) { estudiante ->
            val coincideGrado =
                gradoSeleccionado == "Todas" || estudiante.grado?.toString() == gradoSeleccionado
            val coincideSeccion =
                (seccionSeleccionada == "Todas") || (estudiante.seccion == seccionSeleccionada)
            val nombreCompleto = "${estudiante.nombres ?: ""} ${estudiante.apellidos ?: ""}".lowercase()
            val coincideNombre = queryWords.all { nombreCompleto.contains(it) }
            coincideNombre && coincideGrado && coincideSeccion
        }
        filterEstudiantes.sortWith { e1, e2 ->
            val nombreCompleto1 = "${e1.apellidos} ${e1.nombres}".lowercase()
            val nombreCompleto2 = "${e2.apellidos} ${e2.nombres}".lowercase()
            nombreCompleto1.compareTo(nombreCompleto2)
        }
        estudianteAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        estudianteAdapter = EstudianteAdapter(
            estudiantes = filterEstudiantes,
            onEditClickListenerEstudiante = { estudiante ->
            },
            isEditButtonVisible = userType == "Administrador"
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = estudianteAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
            binding.searchView.requestFocus()
        }
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterEstudiantes(it) }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterEstudiantes(it) }
                return true
            }
        })
    }

    private fun fetchEstudiantes(grado: String, seccion: String) {

        firestore.collection("Estudiante")
            .whereEqualTo("grado", grado.toInt())
            .whereEqualTo("seccion", seccion)
            .get()
            .addOnSuccessListener { result ->
                fullEstudiantesList.clear()
                result.documents.forEach { document ->
                    document.toEstudiante()?.let {
                        fullEstudiantesList.add(it)
                    }
                }
                filterEstudiantes(binding.searchView.query.toString())
            }
    }

    private fun DocumentSnapshot.toEstudiante(): Estudiante? {
        return try {
            Estudiante(
                idEstudiante = getString("idEstudiante") ?: "",
                apellidos = getString("apellidos") ?: "",
                nombres = getString("nombres") ?: "",
                grado = getLong("grado")?.toInt() ?: 0,
                seccion = getString("seccion") ?: "",
                cantidadIncidencias = getLong("cantidadIncidencias")?.toInt() ?: 0
            )
        } catch (e: Exception) {
            Log.e("EstudianteFragment", "Error converting document to Estudiante", e)
            null
        }
    }

    private fun setupButtons() {
        binding.addButtonEstudiante.setOnClickListener {
            startActivity(Intent(context, AddEstudiante::class.java))
        }

        val isAddButtonVisible = when (userType) {
            "Administrador" -> View.VISIBLE
            else -> View.INVISIBLE
        }
        binding.addButtonEstudiante.visibility = isAddButtonVisible
        estudianteAdapter.isEditButtonVisible = userType == "Administrador"
        estudianteAdapter.notifyDataSetChanged()
    }

   fun refreshData() {
        val gradoSeleccionado = binding.spinnerGrado.selectedItem.toString()
        val seccionSeleccionada = binding.spinnerSeccion.selectedItem.toString()
        if (gradoSeleccionado != "Seleccione" && seccionSeleccionada != "Seleccione") {
            fetchEstudiantes(gradoSeleccionado, seccionSeleccionada)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        clearSearchView()

        binding.spinnerGrado.setSelection(0)
        binding.spinnerSeccion.setSelection(0)
        filterEstudiantes(binding.searchView.query.toString())
        refreshData()
    }

    private fun clearSearchView() {
        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
    }
}
