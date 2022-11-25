package com.mytest.composetest.performance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PerformanceViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _contactList = MutableStateFlow<List<ContactModel>>(listOf())
    val contactList = _contactList.asStateFlow()

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState())
    val screenState = _screenState.asStateFlow()

    init {
        val list = mutableListOf<ContactModel>()
        (0..99).forEach {
            list.add(ContactModel(it, "name $it"))
        }
        _contactList.value = list
    }

    fun increaseCount() {
        val currentState = _screenState.value
        _screenState.value = currentState.copy(count = currentState.count + 1)
    }

    fun setSwitchValue(isChecked: Boolean) {
        val currentState = _screenState.value
        _screenState.value = currentState.copy(isChecked = isChecked)
    }

}