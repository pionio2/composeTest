package com.mytest.composetest.coroutinetest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mytest.composetest.util.LogError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlowTestViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val TAG = "FlowTestViewModel"
    }

    private val _testStateFlow = MutableStateFlow<Int>(-1)
    val testStateFlow = _testStateFlow.asStateFlow()


    fun setTestValue(value: Int) {
        _testStateFlow.value = value
    }

    fun printTestValue() {
        LogError(TAG) {"flow test start!!"}
        LogError(TAG) { "testStateFlow value1: ${testStateFlow.value}"}
    }

}


