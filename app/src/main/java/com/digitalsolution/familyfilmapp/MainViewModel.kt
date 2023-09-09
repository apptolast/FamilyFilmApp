package com.digitalsolution.familyfilmapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            delay(100)
            _isLoading.value = false
        }
    }

    fun hideSplashScreen(){
//        Log.d("SPLASH", "hideSplashScreen1: ${_isLoading.value}")
//        _isLoading.value = false
//        Log.d("SPLASH", "hideSplashScreen2: ${_isLoading.value}")
    }
}