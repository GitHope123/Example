package com.example.example.ui.principal1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.example.InicioSesion
import com.example.example.R
import com.example.example.databinding.FragmentPrincipalBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class Principal : Fragment() {

    private var _binding: FragmentPrincipalBinding? = null
    private val binding get() = _binding!!
    private lateinit var nombre: String
    private lateinit var apellido: String
    private lateinit var celular: String
    private lateinit var correo: String
    private lateinit var password: String
    private lateinit var id: String
    private lateinit var userType: String
    private lateinit var firestore: FirebaseFirestore
    private var isTutor: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrincipalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Obtener datos de InicioSesion.GlobalData
        id = InicioSesion.GlobalData.idUsuario
        userType = InicioSesion.GlobalData.datoTipoUsuario
        nombre = InicioSesion.GlobalData.nombresUsuario
        apellido = InicioSesion.GlobalData.apellidosUsuario
        celular = InicioSesion.GlobalData.celularUsuario.toString()
        correo = InicioSesion.GlobalData.correoUsuario
        password = InicioSesion.GlobalData.passwordUsuario
        isTutor = InicioSesion.GlobalData.tutor

        firestore = FirebaseFirestore.getInstance()

        // Configurar el botón
        binding.button.setOnClickListener {
            editPrincipal()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfessorData() // Cargar datos después de que la vista esté creada
    }

    private fun loadProfessorData() {
        lifecycleScope.launch {
            binding.apply {
                textViewNombreCompletoUsuario.text = nombre
                textViewApellidosUsuario.text = apellido
                textViewCelularUsuario.text = celular
                textViewCorreoUsuario.text = correo
                textViewTutorBooleam.text = if (isTutor) "Tutor" else "Docente"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun editPrincipal() {
        val intent = Intent(requireContext(), EditPrincipal::class.java).apply {
            putExtra("nombre", nombre)
            putExtra("apellido", apellido)
            putExtra("celular", celular)
            putExtra("correo", correo)
            putExtra("password", password)
            putExtra("id", id)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Actualizar datos cuando el fragmento vuelve a ser visible
        loadProfessorData()
    }

}
