package com.example.example.ui.Tutorias.estadotutoria

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R

class IncidenciaAdapterTutoria(
    private var incidenciasT:List<TutoriaClass>,
    private val context: Context,
):RecyclerView.Adapter<IncidenciaAdapterTutoria.IncidenciaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidenciaViewHolder {
    val view= LayoutInflater.from(parent.context)
        .inflate(R.layout.item_incidenciatutoria,parent,false)
        return IncidenciaViewHolder(view)
    }

    override fun getItemCount(): Int= incidenciasT.size

    override fun onBindViewHolder(holder: IncidenciaViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
    inner class IncidenciaViewHolder(itemview: View):RecyclerView.ViewHolder(itemview) {

    }
}
