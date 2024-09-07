package com.example.example.ui.Tutorias

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.example.databinding.FragmentTutoriaBinding
import androidx.viewpager.widget.ViewPager
import com.example.example.InicioSesion
import com.example.example.ui.incidencias.estado.IncidenciaRepository
import com.google.android.material.tabs.TabLayout

class Tutoria : Fragment() {
    private var _binding: FragmentTutoriaBinding? = null
    private val binding get() = _binding!!
    private lateinit var tutoriaViewModel: TutoriaViewModel
    private lateinit var grado: String
    private lateinit var seccion: String



    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTutoriaBinding.inflate(inflater, container, false)
        grado=  InicioSesion.GlobalData.gradoUsuario.toString()
        seccion = InicioSesion.GlobalData.seccionUsuario
        tutoriaViewModel = ViewModelProvider(this).get(TutoriaViewModel::class.java)
        tutoriaViewModel.cargarDatos(grado, seccion, TutoriaRepository())

        setupViewPagerAndTabs()

        return binding.root
    }


    private fun setupViewPagerAndTabs() {
        val viewPager: ViewPager = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout

        val adapter = AdapterFragments(childFragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        tutoriaViewModel.cargarDatos(grado, seccion, TutoriaRepository())
        _binding?.viewPager?.post {
            _binding!!.viewPager?.currentItem = 0
        }
    }
}
