package com.example.example.ui.principal1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.example.InicioSesion
import com.example.example.databinding.FragmentPrincipalBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class Principal : Fragment() {

    private var _binding: FragmentPrincipalBinding? = null
    private val binding get() = _binding!!
    private lateinit var nombre: String
    private lateinit var apellido: String
    private  var celular: Long=0
    private lateinit var correo: String
    private lateinit var password: String
    private lateinit var id: String
    private lateinit var userType: String
    private lateinit var celular2:String
    private lateinit var firestore: FirebaseFirestore
    private var isTutor: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        firestore = FirebaseFirestore.getInstance()

        _binding = FragmentPrincipalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        id = InicioSesion.GlobalData.idUsuario
        userType = InicioSesion.GlobalData.datoTipoUsuario
        nombre = InicioSesion.GlobalData.nombresUsuario
        apellido=InicioSesion.GlobalData.apellidosUsuario
        correo = InicioSesion.GlobalData.correoUsuario
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
            firestore.collection("Profesor")
                .document(id)
                .get()
                .addOnSuccessListener { task ->
                    celular = task.getLong("celular")?:0
                    password=task.getString("password")?:""
                    binding.apply {
                        textViewNombreCompletoUsuario.text = nombre
                        textViewApellidosUsuario.text = apellido
                        textViewCelularUsuario.text = celular.toString()
                        textViewCorreoUsuario.text = correo
                        textViewTutorBooleam.text = if (isTutor) "Tutor" else "Docente"
                    celular2=celular.toString()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(),"Error al traer datos",Toast.LENGTH_SHORT).show()
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
            putExtra("celular", celular2)
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
