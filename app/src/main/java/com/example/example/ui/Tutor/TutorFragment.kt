package com.example.example.ui.Tutor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.example.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TutorFragment : Fragment() {

    companion object {
        fun newInstance() = TutorFragment()
    }

    private val viewModel: TutorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Optionally, use the ViewModel here if needed
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tutor, container, false)

        // Initialize the FloatingActionButton and set its click listener
        val addButtonTutor: FloatingActionButton = view.findViewById(R.id.addButtonTutor) // Ensure ID matches XML
        addButtonTutor.setOnClickListener {
            val intent = Intent(requireContext(), AddTutor::class.java) // Use requireContext() for safety
            startActivity(intent)
        }

        return view
    }
}
