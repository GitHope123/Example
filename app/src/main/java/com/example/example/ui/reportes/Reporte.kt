package com.example.example.ui.reportes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.example.R

class Reporte : Fragment() {

    companion object {
        fun newInstance() = Reporte()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_reporte, container, false)

        val button = view.findViewById<ImageButton>(R.id.buttonOpenLink)
        button.setOnClickListener {
            val url = "https://lookerstudio.google.com/reporting/55c2d7b2-0d63-4e02-895a-21ab9c7d018c" // Reemplaza con la URL que desees

            // Copiar la URL al portapapeles
            val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Reporte URL", url)
            clipboardManager.setPrimaryClip(clipData)

            // Mostrar un mensaje de confirmación
            Toast.makeText(requireContext(), "URL copiada al portapapeles", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}

