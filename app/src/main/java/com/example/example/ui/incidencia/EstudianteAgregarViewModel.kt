package com.example.example.ui.incidencia

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class EstudianteAgregarViewModel {
    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
}