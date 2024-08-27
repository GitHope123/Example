package com.example.example.ui.Tutorias.estadoTutoria

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.R
import com.example.example.databinding.FragmentTodosTutoriaBinding
import com.example.example.ui.Tutorias.TutoriaAdapter
import com.example.example.ui.Tutorias.TutoriaClass
import com.example.example.ui.Tutorias.TutoriaRepository
import com.google.firebase.auth.FirebaseAuth

class PendienteTutoria : Fragment() {
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
                tutoriaRepository.getIncidenciasPorGradoSeccion(grado, seccion, "Pendiente") { incidencias ->
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