package com.example.example.ui.Principal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.example.R
import com.example.example.databinding.FragmentPrincipalBinding

import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore

class Principal : Fragment() {

    private var _binding: FragmentPrincipalBinding? = null
    private val binding get() = _binding!!
    private lateinit var nombre:String
    private lateinit var apellido:String
    private lateinit var celular:String
    private lateinit var correo:String
    private lateinit var id:String

    // Firebase references
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var buttonEditar: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        loadProfessorData()
        _binding = FragmentPrincipalBinding.inflate(inflater, container, false)
        val root: View = binding.root
        buttonEditar=root.findViewById(R.id.button)
        editPrincipal()
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
                    if (!documents.isEmpty) {
                        val doc = documents.first()

                        // Obteniendo y estableciendo el nombre y apellidos
                        binding.textViewNombreCompletoUsuario.text = doc.getString("nombres") ?: "N/A"
                        nombre=doc.getString("nombres")?:"N/A"
                        binding.textViewApellidosUsuario.text = doc.getString("apellidos") ?: "N/A"
                        apellido=doc.getString("apellidos")?:"N/A"
                        id=doc.getString("id")?:"N/A"

                        // Obtener y manejar el campo 'celular'
                        val celularValue = doc.get("celular")
                        celular= doc.get("celular").toString()
                        when (celularValue) {
                            is String -> {
                                binding.textViewCelularUsuario.text = celularValue
                            }
                            is Number -> {
                                binding.textViewCelularUsuario.text = celularValue.toString()
                            }
                            else -> {
                                binding.textViewCelularUsuario.text = "N/A"
                            }
                        }

                        // Establecer el correo electrónico
                        binding.textViewCorreoUsuario.text = doc.getString("correo") ?: "N/A"
                        correo=doc.getString("correo")?:"N/A"
                        // Obtener y manejar el campo 'tutor' (booleano)
                        val isTutor = doc.getBoolean("tutor") ?: false
                        binding.textViewTutorBooleam.text = if (isTutor) {
                            "Tutor"
                        } else {
                            "Docente"
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Manejar cualquier error aquí
                    exception.printStackTrace()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun editPrincipal(){
        buttonEditar.setOnClickListener{
            val intent= Intent(requireContext(),EditPrincipal::class.java)
            intent.putExtra("nombre",nombre)
            intent.putExtra("apellido",apellido)
            intent.putExtra("celular",celular)
            intent.putExtra("correo",correo)
            intent.putExtra("id",id)
            startActivity(intent)
        }

    }
    fun refreshData() {
        loadProfessorData()
    }
    override fun onResume() {
        super.onResume()
        refreshData()
    }
}
