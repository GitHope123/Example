package com.example.example.ui.incidencias.estado

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import com.example.example.ui.incidencias.DescripcionIncidencia

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
            val intent = Intent(context, DescripcionIncidencia::class.java).apply {
                putExtra("INCIDENCIA_ID", incidencia.id)
                putExtra("INCIDENCIA_FECHA", incidencia.fecha)
                putExtra("INCIDENCIA_HORA", incidencia.hora)
                putExtra("INCIDENCIA_NOMBRE", incidencia.nombreEstudiante)
                putExtra("INCIDENCIA_APELLIDO", incidencia.apellidoEstudiante)
                putExtra("INCIDENCIA_GRADO", incidencia.grado)
                putExtra("INCIDENCIA_SECCION", incidencia.seccion)
                putExtra("INCIDENCIA_TIPO", incidencia.tipo)
                putExtra("INCIDENCIA_GRAVEDAD", incidencia.gravedad)
                putExtra("INCIDENCIA_ESTADO", incidencia.estado)
                putExtra("INCIDENCIA_DETALLE", incidencia.detalle)
                incidencia.imageUri?.let { uri ->
                    putExtra("INCIDENCIA_FOTO_URL", uri)
                }
            }
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
        private val tvImagenGravedad: ImageView = itemView.findViewById(R.id.tvImagenGravedad)
        private val tvGrado: TextView = itemView.findViewById(R.id.tvGrado)
        private val tvSeccion: TextView = itemView.findViewById(R.id.tvSeccion)
        private val tvTipo: TextView = itemView.findViewById(R.id.tvTipo)

        fun bind(incidencia: IncidenciaClass) {
            tvNombre.text = "${incidencia.apellidoEstudiante} ${incidencia.nombreEstudiante}"
            tvGravedad.text = incidencia.gravedad
            tvHora.text = incidencia.hora
            tvFecha.text = incidencia.fecha
            tvEstado.text = incidencia.estado
            tvGrado.text = incidencia.grado.toString()
            tvSeccion.text = incidencia.seccion
            tvTipo.text = incidencia.tipo

            tvEstado.setTextColor(
                if (incidencia.estado == "Revisado")
                    itemView.context.getColor(R.color.Green)
                else
                    itemView.context.getColor(R.color.color_red)
            )

            when (incidencia.gravedad) {
                "Moderado" -> {
                    tvGravedad.setTextColor(itemView.context.getColor(R.color.Primary_yellow))
                    tvImagenGravedad.setColorFilter(itemView.context.getColor(R.color.Primary_yellow))
                }
                "Grave" -> {
                    tvGravedad.setTextColor(itemView.context.getColor(R.color.color_orange))
                    tvImagenGravedad.setColorFilter(itemView.context.getColor(R.color.color_orange))
                }
                else -> {
                    tvGravedad.setTextColor(itemView.context.getColor(R.color.color_red))
                    tvImagenGravedad.setColorFilter(itemView.context.getColor(R.color.color_red))
                }
            }
        }
    }
}
