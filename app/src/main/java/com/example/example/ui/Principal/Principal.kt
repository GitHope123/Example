package com.example.example.ui.Principal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.example.databinding.FragmentPrincipalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

        // No cargar datos aquí para evitar operaciones innecesarias
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
            try {
                val currentUser = auth.currentUser
                val userEmail = currentUser?.email ?: return@launch

                // Ejecutar la consulta Firestore en el hilo IO y optimizar la espera
                val documents = withContext(Dispatchers.IO) {
                    firestore.collection("Profesor")
                        .whereEqualTo("correo", userEmail)
                        .limit(1)
                        .get()
                        .await()
                }

                if (documents.isEmpty) return@launch

                val doc = documents.first()

                // Asignar las variables en un solo bloque
                id = doc.getString("id") ?: "N/A"
                nombre = doc.getString("nombres") ?: "N/A"
                apellido = doc.getString("apellidos") ?: "N/A"
                celular = doc.get("celular").toString()
                correo = doc.getString("correo") ?: "N/A"
                val isTutor = doc.getBoolean("tutor") ?: false

                // Actualizar la UI directamente en el hilo principal
                withContext(Dispatchers.Main) {
                    binding.apply {
                        textViewNombreCompletoUsuario.text = nombre
                        textViewApellidosUsuario.text = apellido
                        textViewCelularUsuario.text = celular
                        textViewCorreoUsuario.text = correo
                        textViewTutorBooleam.text = if (isTutor) "Tutor" else "Docente"
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                // Manejo de errores adicional, como mostrar un mensaje al usuario
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al cargar los datos del profesor", Toast.LENGTH_SHORT).show()
                }
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
        // Actualizar datos cuando el fragmento vuelve a ser visible
        if (_binding != null) {
            loadProfessorData()
        }
    }
}
