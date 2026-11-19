package com.example.activity7.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.activity7.repositori.RepositoriSiswa

class EditViewModel (
    savedStateHandle: SavedStateHandle,
    private val repositoriSiswa: RepositoriSiswa): ViewModel() {

    var uiStateSiswa by mutableStateOf(UIStateSiswa())
        private set



}

