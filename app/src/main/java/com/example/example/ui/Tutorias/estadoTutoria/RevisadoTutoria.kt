package com.example.example.ui.Tutorias.estadoTutoria

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.BarraLateral
import com.example.example.InicioSesion
import com.example.example.R
import com.example.example.databinding.FragmentRevisadoTutoriaBinding
import com.example.example.databinding.FragmentTodosTutoriaBinding
import com.example.example.ui.Tutorias.TutoriaAdapter
import com.example.example.ui.Tutorias.TutoriaClass
import com.example.example.ui.Tutorias.TutoriaRepository
import com.example.example.ui.Tutorias.TutoriaViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RevisadoTutoria : Fragment() {
    private var _binding: FragmentRevisadoTutoriaBinding? = null
    private val binding get() = _binding!!
    private lateinit var tutoriaAdapter: TutoriaAdapter
    private val listaTutorias  : MutableList<TutoriaClass> = mutableListOf()
    private val listaFilter  : MutableList<TutoriaClass> = mutableListOf()
    private val tutoriaRepository = TutoriaRepository()
    private var currentFilter: String = "Todos"
    private lateinit var idUsuario:String
    private lateinit var tutoriaViewModel: TutoriaViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRevisadoTutoriaBinding.inflate(inflater, container, false)
        tutoriaViewModel = ViewModelProvider(requireParentFragment()).get(TutoriaViewModel::class.java)

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
        loadTutoriasForTutor(idUsuario, selection)
    }
    private fun loadTutoriasForTutor(id: String, filtroFecha: String) {
        tutoriaViewModel.filtrarIncidenciasPorEstado("Revisado",filtroFecha)
        tutoriaViewModel.incidenciasFiltradasLiveData.observe(viewLifecycleOwner) { incidencias ->
            listaTutorias.clear() // Asegurarse de limpiar la lista antes
            listaTutorias.addAll(incidencias) // Agregar los datos cargados
            tutoriaAdapter.updateData(listaTutorias) // Actualizar la vista
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