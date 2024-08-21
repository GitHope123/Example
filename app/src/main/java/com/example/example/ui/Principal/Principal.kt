package com.example.example.ui.Principal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.example.databinding.FragmentPrincipalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Principal : Fragment() {

    private var _binding: FragmentPrincipalBinding? = null
    private val binding get() = _binding!!
    private lateinit var nombre: String
    private lateinit var apellido: String
    private lateinit var celular: String
    private lateinit var correo: String
    private lateinit var id: String

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrincipalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        loadProfessorData()
        binding.button.setOnClickListener {
            editPrincipal()
        }

        return root
    }

    private fun loadProfessorData() {
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email

        if (userEmail != null) {
            firestore.collection("Profesor")
                .whereEqualTo("correo", userEmail)
                .get()
                .addOnSuccessListener { documents ->
                    if (_binding != null && !documents.isEmpty) {
                        val doc = documents.first()

                        nombre = doc.getString("nombres") ?: "N/A"
                        apellido = doc.getString("apellidos") ?: "N/A"
                        id = doc.getString("id") ?: "N/A"
                        binding.textViewNombreCompletoUsuario.text = nombre
                        binding.textViewApellidosUsuario.text = apellido

                        celular = doc.get("celular").toString()
                        binding.textViewCelularUsuario.text = celular

                        correo = doc.getString("correo") ?: "N/A"
                        binding.textViewCorreoUsuario.text = correo

                        val isTutor = doc.getBoolean("tutor") ?: false
                        binding.textViewTutorBooleam.text = if (isTutor) "Tutor" else "Docente"
                    }
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
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
            putExtra("id", id)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) {
            loadProfessorData()
        }
    }
}
