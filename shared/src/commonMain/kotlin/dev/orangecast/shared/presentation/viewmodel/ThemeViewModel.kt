package dev.orangecast.shared.presentation.viewmodel

import dev.orangecast.shared.domain.model.ThemeMode
import dev.orangecast.shared.domain.repository.ThemeRepository
import dev.orangecast.shared.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val themeRepository: ThemeRepository
) : BaseViewModel() {
    
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadThemeMode()
        observeThemeChanges()
    }
    
    fun setThemeMode(mode: ThemeMode) {
        if (_isLoading.value) return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = themeRepository.setThemeMode(mode)
                if (result.isSuccess) {
                    _themeMode.value = mode
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun loadThemeMode() {
        viewModelScope.launch {
            try {
                _themeMode.value = themeRepository.getThemeMode()
            } catch (e: Exception) {
                _themeMode.value = ThemeMode.SYSTEM
            }
        }
    }
    
    private fun observeThemeChanges() {
        viewModelScope.launch {
            themeRepository.observeThemeMode().collect { mode ->
                _themeMode.value = mode
            }
        }
    }
}