package com.example.example.ui.Profesor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.example.databinding.FragmentProfesorBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class ProfesorFragment : Fragment() {

    private var _binding: FragmentProfesorBinding? = null
    private val binding get() = _binding!!

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var profesorAdapter: ProfesorAdapter
    private val profesorList = mutableListOf<Profesor>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfesorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize RecyclerView and Adapter
        profesorAdapter = ProfesorAdapter(profesorList)
        binding.recyclerViewProfesores.apply {
            adapter = profesorAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Fetch data from Firestore
        fetchProfesores()

        return root
    }

    private fun fetchProfesores() {
        firestore.collection("Docente")
            .get()
            .addOnSuccessListener { result ->
                profesorList.clear() // Clear existing data
                for (document in result) {
                    val profesor = document.toObject<Profesor>()
                    profesorList.add(profesor)
                }
                profesorAdapter.notifyDataSetChanged() // Notify adapter of data changes
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                exception.printStackTrace()
                // You might want to show an error message to the user here
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
