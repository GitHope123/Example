package com.example.example.ui.Tutorias.estadotutoria

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class AdapterEstado (
    fragmentManager:FragmentManager
): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    override fun getCount(): Int {
    return 3
    }

    override fun getItem(position: Int): Fragment {
      return when(position){
          0-> TodoTutoria()
          1->PendienteTutoria()
          else->RevisadoTutoria()
      }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0->"Todos"
            1->"Pendientes"
            else->"Revisados"
        }
    }
}