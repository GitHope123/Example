package com.example.example.ui.incidencias

import IncidenciaViewModel
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.example.InicioSesion
import com.example.example.R
import com.example.example.ui.incidencias.estado.AdapterEstado
import com.example.example.ui.incidencias.estado.IncidenciaRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class Incidencia : Fragment() {

    private lateinit var btnAgregar: FloatingActionButton
    private lateinit var incidenciaViewModel: IncidenciaViewModel
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private lateinit var idUsuario: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Asegurarse de que el ID de usuario esté disponible antes de que se cree la vista
        idUsuario = InicioSesion.GlobalData.idUsuario
        incidenciaViewModel = ViewModelProvider(this).get(IncidenciaViewModel::class.java)
        incidenciaViewModel.cargarIncidencias(idUsuario, IncidenciaRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_incidencia, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)
        btnAgregar = view.findViewById(R.id.btnAgregarIncidencia)!!

        viewPager?.adapter = AdapterEstado(childFragmentManager)
        tabLayout?.setupWithViewPager(viewPager)

        // Establecer la pestaña "Todos" como la predeterminada
        viewPager?.post {
            viewPager?.currentItem = 0
        }

        init()
        return view
    }

    private fun init() {
        btnAgregar.setOnClickListener {
            val intent = Intent(requireContext(), AgregarEstudiantes::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar incidencias y asegurarse de que la pestaña "Todos" esté seleccionada
        incidenciaViewModel.cargarIncidencias(idUsuario, IncidenciaRepository())

        // Asegurarse de que siempre se seleccione la pestaña "Todos" al regresar
        viewPager?.post {
            viewPager?.currentItem = 0
        }
    }
}
