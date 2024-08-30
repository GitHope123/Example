package com.example.example.ui.Tutorias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.InicioSesion
import com.example.example.R
import com.example.example.databinding.FragmentTodosTutoriaBinding
import com.google.firebase.auth.FirebaseAuth

class TodosTutoria : Fragment() {

    private var _binding: FragmentTodosTutoriaBinding? = null
    private val binding get() = _binding!!
    private lateinit var tutoriaAdapter: TutoriaAdapter
    private val listaTutorias = mutableListOf<TutoriaClass>()
    private val tutoriaRepository = TutoriaRepository()
    private var currentFilter: String = "Todos"
    private lateinit var idUsuario:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodosTutoriaBinding.inflate(inflater, container, false)
        init()
        resetAutoComplete()
        val email = FirebaseAuth.getInstance().currentUser?.email
        email?.let {
            loadTutoriasForTutor(it, currentFilter) // Se pasa "Todos" como filtro por defecto
        }
        return binding.root
    }


    private fun init() {

        binding.recyclerViewTutorias.layoutManager = LinearLayoutManager(context)
        tutoriaAdapter = TutoriaAdapter(listaTutorias)
        binding.recyclerViewTutorias.adapter = tutoriaAdapter
    }

    private fun resetAutoComplete() {
        val fechaTutoria = resources.getStringArray(R.array.item_fecha_tutoria)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_tutoria, fechaTutoria)
        binding.autoComplete.setAdapter(arrayAdapter)
        binding.autoComplete.text.clear()
        binding.autoComplete.setText(currentFilter, false)
        binding.autoComplete.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            applyFilter(selectedItem)
        }
    }

    private fun applyFilter(selection: String) {
        currentFilter= selection
        idUsuario = InicioSesion.GlobalData.idUsuario
        loadTutoriasForTutor(idUsuario, selection) // Usar 'selection' directamente
    }

    private fun loadTutoriasForTutor(id: String, filtroFecha: String) {
        tutoriaRepository.getGradoSeccionTutorByEmail(id) { grado, seccion ->
            tutoriaRepository.getIncidenciasPorGradoSeccion(grado, seccion, "", filtroFecha) { incidencias ->
                if (isAdded) {
                    listaTutorias.clear()
                    listaTutorias.addAll(incidencias)
                    tutoriaAdapter.notifyDataSetChanged()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpia la referencia a la vista enlazada
    }

    override fun onResume() {
        super.onResume()
        currentFilter = "Todos"
        resetAutoComplete()
        idUsuario = InicioSesion.GlobalData.idUsuario
        loadTutoriasForTutor(idUsuario, currentFilter)
    }
}