package com.example.composetest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _error = MutableLiveData(false)
    val error: LiveData<Boolean> = _error

    fun onNameChange(newName: String) {
        _name.value = newName
        _error.value = newName == "error"
    }
}