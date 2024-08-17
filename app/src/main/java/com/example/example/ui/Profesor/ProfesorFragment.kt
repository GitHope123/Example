package com.example.example.ui.Profesor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.example.databinding.FragmentProfesorBinding
import com.google.firebase.firestore.FirebaseFirestore

class ProfesorFragment : Fragment() {

    private var _binding: FragmentProfesorBinding? = null
    private val binding get() = _binding!!

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfesorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Access the Docente collection from Firestore
        firestore.collection("Docente")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    // Handle the document data
                    val nombre = document.getString("nombre") ?: "N/A"
                    val apellidos = document.getString("apellidos") ?: "N/A"
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                exception.printStackTrace()
            }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
