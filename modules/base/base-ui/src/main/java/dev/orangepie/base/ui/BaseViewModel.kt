package dev.orangepie.base.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.orangepie.base.ui.navigation.NavCommand
import dev.orangepie.base.ui.navigation.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

abstract class BaseViewModel : ViewModel() {

    fun <T> Flow<T>.stateInViewModel(
        initialValue: T,
        scope: CoroutineScope = viewModelScope,
        started: SharingStarted = SharingStarted.WhileSubscribed(5000L),
    ): StateFlow<T> {
        return stateIn(scope, started, initialValue)
    }

    fun navigate(navCommand: NavCommand) {
        Navigator.navigateTo(navCommand)
    }
}