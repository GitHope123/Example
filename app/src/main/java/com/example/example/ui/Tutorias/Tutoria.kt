package com.example.example.ui.Tutorias

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.example.R
import com.example.example.databinding.FragmentTutoriaBinding
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class Tutoria : Fragment() {
    private var _binding: FragmentTutoriaBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTutoriaBinding.inflate(inflater, container, false)

        resetAutoComplete()
        setupViewPagerAndTabs()

        return binding.root
    }

    private fun resetAutoComplete() {
        val fechaTutoria = resources.getStringArray(R.array.item_fecha_tutoria)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.list_tutoria, fechaTutoria)
        binding.autoComplete.setAdapter(arrayAdapter)
        binding.autoComplete.text.clear()
        binding.autoComplete.setText("Todos", false)
    }

    private fun setupViewPagerAndTabs() {
        val viewPager: ViewPager = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout

        val adapter = AdapterFragments(childFragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
