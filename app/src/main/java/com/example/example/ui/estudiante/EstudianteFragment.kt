package com.example.example.ui.estudiante
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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class EstudianteFragment : Fragment() {
    private var _binding: FragmentEstudianteBinding? = null
    private val binding get() = _binding!!
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private lateinit var estudianteAdapter: EstudianteAdapter
    private val filterEstudiantes = mutableListOf<Estudiante>()
    private val fullEstudiantesList = mutableListOf<Estudiante>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEstudianteBinding.inflate(inflater, container, false)
        updateGrado()
        setupRecyclerView()
        setupSearchView()
        fetchEstudiantes()
        setupButtons()
        return binding.root
    }


    private fun updateGrado() {
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
            if(gradoSeleccionado=="1"){
                arrayOf("Todas","A","B","C","D","E")
            }
            else{
                arrayOf("Todas","A","B","C","D")
            }
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
                (seccionSeleccionada == "Todas") || (estudiante.seccion == seccionSeleccionada)
            val nombreCompleto = "${estudiante.nombres ?: ""} ${estudiante.apellidos ?: ""}".lowercase()
            val coincideNombre = queryWords.all { nombreCompleto.contains(it) }
            coincideNombre && coincideGrado && coincideSeccion
        }

        estudianteAdapter.notifyDataSetChanged()
    }


    private fun setupRecyclerView() {
        estudianteAdapter = EstudianteAdapter(filterEstudiantes){

        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
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
        firestore.collection("Estudiante")
            .get()
            .addOnSuccessListener { result->
                fullEstudiantesList.clear()
                result.documents.forEach(){document->
                    document.toEstudiante()?.let {
                        fullEstudiantesList.add(it)
                    }
                }
                filterEstudiantes.clear()
                filterEstudiantes.addAll(fullEstudiantesList)
                estudianteAdapter.notifyDataSetChanged()

            }

    }

    private fun DocumentSnapshot.toEstudiante():Estudiante?{
        return try {
            Estudiante(
                idEstudiante = getString("idEstudiante")?:"",
                apellidos = getString("apellidos")?:"",
                nombres=getString("nombres")?:"",
                celularApoderado = getLong("celularApoderado")?:0L,
                dni = getLong("dni")?:0L,
                grado = getLong("grado")?.toInt()?:0,
                seccion = getString("seccion")?:"",
                cantidadIncidencias = getLong("cantidadInciddencias")?.toInt()?:0
            )

        }catch (e: Exception) {
            Log.e("ProfesorFragment", "Error converting document to Profesor", e)
            null
        }

    }

    private fun setupButtons() {
        binding.addButtom.setOnClickListener {
            startActivity(Intent(requireContext(), AddEstudiante::class.java))
        }

    }

    fun refreshData() {
        fetchEstudiantes()
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
        clearSearchView() // Limpiar el SearchView al pausar el fragmento
    }

    private fun clearSearchView() {
        binding.searchView.setQuery("", false) // Limpiar el texto sin activar la búsqueda nuevamente
        binding.searchView.clearFocus() // Opcional: para eliminar el foco del SearchView
    }
}