package com.example.example.ui.Tutorias

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R
import androidx.core.content.ContextCompat

class TutoriaAdapter(private var listaTutorias: List<TutoriaClass>) :
    RecyclerView.Adapter<TutoriaAdapter.TutoriaViewHolder>() {

    inner class TutoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagenViewAlerta: ImageView = itemView.findViewById(R.id.ImagenViewAlertaTutoria)
        private val textViewGravedad: TextView = itemView.findViewById(R.id.TextViewGravedadTutoria)
        private val textViewFecha: TextView = itemView.findViewById(R.id.textViewFechaTutoria)
        private val textViewHora: TextView = itemView.findViewById(R.id.textViewHoraTutoria)
        private val textViewEstudiante: TextView = itemView.findViewById(R.id.textViewEstudianteTutoria)
        private val textViewProfesor: TextView = itemView.findViewById(R.id.textViewProfesorTutoria)
        private val textViewCurso: TextView = itemView.findViewById(R.id.textViewCursoTutoria)
        private val textViewEstado: TextView = itemView.findViewById(R.id.textViewEstadoTutoria)

        fun bind(tutoria: TutoriaClass) {
            textViewFecha.text = tutoria.fecha
            textViewHora.text = tutoria.hora
            textViewEstudiante.text = "${tutoria.nombreEstudiante} ${tutoria.apellidoEstudiante}"
            textViewProfesor.text = "${tutoria.nombreProfesor} ${tutoria.apellidoProfesor}"
            textViewCurso.text = "${tutoria.grado} ${tutoria.seccion}"
            textViewEstado.text = tutoria.estado
            textViewEstado.setTextColor(getEstadoColorText(tutoria.estado))

            textViewGravedad.text = tutoria.gravedad
            setAlertColors(getAlertColor(tutoria.gravedad))

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DescripcionRevisar::class.java).apply {
                    putExtra("TUTORIA_EXTRA", tutoria) // Pasa el objeto TutoriaClass a la actividad
                }
                itemView.context.startActivity(intent)
            }
        }

        private fun getEstadoColorText(estado: String): Int {
            return when (estado) {
                "Pendiente" -> ContextCompat.getColor(itemView.context, R.color.color_red)
                "Revisado" -> ContextCompat.getColor(itemView.context, R.color.Green)
                else -> ContextCompat.getColor(itemView.context, R.color.Secondary_brown)
            }
        }

        private fun getAlertColor(gravedad: String): Int {
            return when (gravedad) {
                "Moderado" -> ContextCompat.getColor(itemView.context, R.color.Primary_yellow)
                "Grave" -> ContextCompat.getColor(itemView.context, R.color.color_orange)
                else -> ContextCompat.getColor(itemView.context, R.color.color_red)
            }
        }

        private fun setAlertColors(color: Int) {
            imagenViewAlerta.setColorFilter(color)
            textViewGravedad.setTextColor(color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutoriaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tutoria, parent, false)
        return TutoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TutoriaViewHolder, position: Int) {
        holder.bind(listaTutorias[position])
    }

    fun updateData(newListTutoria: List<TutoriaClass>) {
        listaTutorias = newListTutoria
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listaTutorias.size
    }
}
