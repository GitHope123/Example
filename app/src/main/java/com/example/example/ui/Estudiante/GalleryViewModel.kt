package com.example.example.ui.Estudiante

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {
//Obtencion de datos desde Firestore
    private val _text = MutableLiveData<String>().apply {



    }
    val text: LiveData<String> = _text
}