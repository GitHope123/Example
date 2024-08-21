package com.example.example.ui.Estudiante
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.databinding.FragmentEstudianteBinding
import com.example.example.ui.Incidencia.EstudianteAgregar
import com.google.firebase.firestore.FirebaseFirestore
class EstudianteFragment : Fragment() {
    private var _binding: FragmentEstudianteBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private lateinit var estudianteAdapter: EstudianteAdapter
    private val filterEstudiantes = mutableListOf<Estudiante>()
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
        initButton()
        setupRecyclerView()
        setupSearchView()
        fetchEstudiantes()
        setupButtons()
    }

    private fun initButton() {
        val grados= arrayOf("Todas","1","2","3","4","5")
        val adapterGrados=ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,grados)
        adapterGrados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGrado.adapter=adapterGrados
        binding.spinnerGrado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val gradoSeleccionado = binding.spinnerGrado.selectedItem.toString()
                updateSecciones(gradoSeleccionado)
                filterEstudiante(binding.searchView.query.toString())

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where no item is selected
            }
        }
        binding.spinnerSeccion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                filterEstudiante(binding.searchView.query.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateSecciones(gradoSeleccionado: String) {
        val secciones = if (gradoSeleccionado == "Todas") {
            arrayOf("Todas")
        } else {
            arrayOf("Todas", "A", "B", "C", "D", "E")
        }
        val adapterSecciones = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, secciones)
        adapterSecciones.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSeccion.adapter = adapterSecciones
        binding.spinnerSeccion.isEnabled = gradoSeleccionado != "Todas"
    }
    private fun filterEstudiante(query: String) {
        val gradoSeleccionado = binding.spinnerGrado.selectedItem?.toString()
        val seccionSeleccionada = binding.spinnerSeccion.selectedItem?.toString()
        val queryWords = query.lowercase().split("\\s+".toRegex())

        filterEstudiantes.clear()
        fullEstudiantesList.filterTo(filterEstudiantes) { estudiante ->
            val coincideGrado =
                gradoSeleccionado == "Todas" || estudiante.grado?.toString() == gradoSeleccionado
            val coincideSeccion =
                (seccionSeleccionada == "Todas") || (estudiante.seccion?.toString() == seccionSeleccionada)
            val nombreCompleto = "${estudiante.nombres ?: ""} ${estudiante.apellidos ?: ""}".lowercase()
            val coincideNombre = queryWords.all { nombreCompleto.contains(it) }
            coincideNombre && coincideGrado && coincideSeccion

        }

        estudianteAdapter.notifyDataSetChanged()
    }


    private fun setupRecyclerView() {
        estudianteAdapter = EstudianteAdapter(filterEstudiantes, requireContext()) // Corregido el constructor
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = estudianteAdapter
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = estudianteAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
            binding.searchView.requestFocus()
        }

        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterEstudiante(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterEstudiante(it) }
                return true
            }
        })
    }

    private fun fetchEstudiantes() {
        firestore.collection("Aula")
            .get()
            .addOnSuccessListener { result ->
                fullEstudiantesList.clear()
                for (document in result) {
                    val estudiantes = document["estudiantes"] as? List<Map<String, Any>> ?: continue
                    estudiantes.forEach { estudiante ->
                        val id = estudiante["nombres"] as? String ?: ""
                        val apellidos = estudiante["apellidos"] as? String ?: ""
                        val nombres = estudiante["nombres"] as? String ?: ""
                        val celular = (estudiante["celularApoderado"] as? Long) ?: 0
                        val dni = (estudiante["celularApoderado"] as? Long)?: 0
                        val grado = (estudiante["grado"] as? Long)?.toInt() ?: 0
                        val seccion = estudiante["seccion"] as? String ?: ""
                        fullEstudiantesList.add(Estudiante(id,apellidos,nombres,celular,dni,grado,seccion))
                    }
                }
                filterEstudiantes.addAll(fullEstudiantesList)
                estudianteAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Manejo de errores
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


    private fun showProgressBar(show: Boolean) {
        // Verificar que binding no sea null antes de usarlo
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setupButtons() {
        binding.addButtom.setOnClickListener {
            startActivity(Intent(requireContext(), AddEstudiante::class.java))
        }

    }

    fun refreshData() {
        fetchEstudiantes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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