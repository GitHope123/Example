package com.example.example.ui.Tutorias

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


        setupViewPagerAndTabs()

        return binding.root
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
