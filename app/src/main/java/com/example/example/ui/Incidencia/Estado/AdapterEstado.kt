package com.example.example.ui.Incidencia.Estado

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class AdapterEstado (private val context: Context, fragmentManager: FragmentManager, internal val totalTabs: Int):
    FragmentPagerAdapter(fragmentManager){
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0-> Todo()
            1-> Pendiente()
            2-> Revisado()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0->"Todos"
            1->"Pendientes"
            2->"Revisados"
        }

}