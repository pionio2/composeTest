package com.example.composetest

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _error = MutableLiveData(false)
    val error: LiveData<Boolean> = _error

    private val _timeoutText = MutableStateFlow("Timeout!!")
    val timeoutText: StateFlow<String> = _timeoutText

    fun onNameChange(newName: String) {
        _name.value = newName
        _error.value = newName == "error"
    }

    fun changeTimeoutText(timeoutText:String) {
        viewModelScope.launch {
            _timeoutText.emit(timeoutText)
        }
    }
}