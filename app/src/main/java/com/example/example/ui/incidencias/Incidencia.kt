package com.example.example.ui.incidencias

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.example.example.R
import com.example.example.ui.incidencias.estado.AdapterEstado
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class Incidencia : Fragment() {

    private lateinit var adapter :AdapterEstado
    private lateinit var btnAgregar : FloatingActionButton
    var tabLayout : TabLayout?=null
    var viewPager: ViewPager?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_incidencia, container, false)
        tabLayout = view?.findViewById(R.id.tabLayout)
        viewPager = view?.findViewById(R.id.viewPager)
        btnAgregar = view?.findViewById(R.id.btnAgregarIncidencia)!!
        viewPager!!.adapter = AdapterEstado(childFragmentManager)
        tabLayout!!.setupWithViewPager(viewPager)
        init()

        return view
    }
    private fun init() {
        btnAgregar.setOnClickListener {
            val intent= Intent(requireContext(), AgregarEstudiantes::class.java)
            startActivity(intent)
        }
    }
}