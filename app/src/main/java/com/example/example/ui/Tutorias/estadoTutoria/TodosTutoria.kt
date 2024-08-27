package com.example.example.ui.Tutorias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.databinding.FragmentTodosTutoriaBinding
import com.google.firebase.auth.FirebaseAuth

class TodosTutoria : Fragment() {

    private var _binding: FragmentTodosTutoriaBinding? = null
    private val binding get() = _binding!!
    private lateinit var tutoriaAdapter: TutoriaAdapter
    private val listaTutorias = mutableListOf<TutoriaClass>()
    private val tutoriaRepository = TutoriaRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodosTutoriaBinding.inflate(inflater, container, false)

        // Configuración del RecyclerView
        binding.recyclerViewTutorias.layoutManager = LinearLayoutManager(context)
        tutoriaAdapter = TutoriaAdapter(listaTutorias)
        binding.recyclerViewTutorias.adapter = tutoriaAdapter

        // Obtener el correo del tutor autenticado
        val email = FirebaseAuth.getInstance().currentUser?.email
        email?.let {
            loadTutoriasForTutor(it)
        }

        return binding.root
    }

    private fun loadTutoriasForTutor(email: String) {
        // Obtener grado y sección del tutor
        tutoriaRepository.getGradoSeccionTutorByEmail(email) { grado, seccion ->
            // Filtrar incidencias por grado, sección y estado 'Pendiente'
            tutoriaRepository.getIncidenciasPorGradoSeccion(grado, seccion, "") { incidencias ->
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
        _binding = null
    }
}
