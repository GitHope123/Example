package com.example.example.ui.Incidencia.Estado

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.Incidencia.DescripcionIncidencia
import android.content.Context

class IncidenciaAdapter(
    private var incidencias: List<IncidenciaClass>,
    private val context: Context
) : RecyclerView.Adapter<IncidenciaAdapter.IncidenciaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidenciaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incidencia, parent, false)
        return IncidenciaViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncidenciaViewHolder, position: Int) {
        val incidencia = incidencias[position]
        holder.bind(incidencia)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DescripcionIncidencia::class.java)
            intent.putExtra("INCIDENCIA_DATA", incidencia)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = incidencias.size

    fun updateData(newIncidencias: List<IncidenciaClass>) {
        incidencias = newIncidencias
        notifyDataSetChanged()
    }

    inner class IncidenciaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvEstudiante)
        private val tvGravedad: TextView = itemView.findViewById(R.id.tvGravedad)
        private val tvHora: TextView = itemView.findViewById(R.id.tvHora)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)


        fun bind(incidencia: IncidenciaClass) {
            tvNombre.text = "${incidencia.nombreEstudiante} ${incidencia.apellidoEstudiante}"
            tvGravedad.text = incidencia.gravedad
            tvHora.text = incidencia.hora
            tvFecha.text = incidencia.fecha
            tvEstado.text = incidencia.estado

        }
    }
}
