package com.example.example.ui.Tutoria

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.example.R
import com.example.example.databinding.FragmentTutoriaBinding

class Tutoria : Fragment() {
    private var _binding: FragmentTutoriaBinding? = null
    private val binding get() = _binding!!
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentTutoriaBinding.inflate(inflater, container, false)
        val fechaTutoria = resources.getStringArray(R.array.item_fecha_tutoria)
        val arrayAdapter= ArrayAdapter(requireContext(), R.layout.list_tutoria,fechaTutoria)
        binding.autoComplete.setAdapter(arrayAdapter)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}