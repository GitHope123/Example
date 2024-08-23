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
import android.widget.ImageView

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
            intent.putExtra("INCIDENCIA_ID", incidencia.id) // Asegúrate de enviar el ID si es necesario
            intent.putExtra("INCIDENCIA_FECHA", incidencia.fecha)
            intent.putExtra("INCIDENCIA_HORA", incidencia.hora)
            intent.putExtra("INCIDENCIA_NOMBRE", incidencia.nombreEstudiante)
            intent.putExtra("INCIDENCIA_APELLIDO", incidencia.apellidoEstudiante)
            intent.putExtra("INCIDENCIA_GRADO", incidencia.grado)
            intent.putExtra("INCIDENCIA_SECCION", incidencia.seccion)
            intent.putExtra("INCIDENCIA_TIPO", incidencia.tipo)
            intent.putExtra("INCIDENCIA_GRAVEDAD", incidencia.gravedad)
            intent.putExtra("INCIDENCIA_ESTADO", incidencia.estado)
            intent.putExtra("INCIDENCIA_DETALLE", incidencia.detalle)
            incidencia.imageUri?.let { uri ->
                intent.putExtra("INCIDENCIA_FOTO_URL", uri)
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
            tvNombre.text = "${incidencia.nombreEstudiante} ${incidencia.apellidoEstudiante}"
            tvGravedad.text = incidencia.gravedad
            tvHora.text = incidencia.hora
            tvFecha.text = incidencia.fecha
            tvEstado.text = incidencia.estado
            tvGrado.text = incidencia.grado.toString()
            tvSeccion.text = incidencia.seccion
            tvTipo.text = incidencia.tipo
            when (incidencia.estado) {
                "Revisado" -> tvEstado.setTextColor(itemView.context.getColor(R.color.Green))
                else -> tvEstado.setTextColor(itemView.context.getColor(R.color.color_red))
            }
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
