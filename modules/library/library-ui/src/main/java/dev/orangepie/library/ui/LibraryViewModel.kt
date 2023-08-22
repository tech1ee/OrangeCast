package dev.orangepie.library.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.orangepie.base.ui.BaseViewModel
import dev.orangepie.base.ui.navigation.NavCommand
import dev.orangepie.base.ui.navigation.NavRoutes
import dev.orangepie.base.ui.navigation.Navigator
import dev.orangepie.library.domain.usecase.PodcastLibraryStateUseCase
import dev.orangepie.library.ui.mapper.LibraryUIMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getLibrary: PodcastLibraryStateUseCase,
    private val libraryUIMapper: LibraryUIMapper,
) : BaseViewModel() {

    private val viewModelState = MutableStateFlow(LibraryViewModelState())
    val uiState = viewModelState
        .map(libraryUIMapper::toUIState)
        .stateInViewModel(libraryUIMapper::toUIStateBlocking)

    init {
        getLibrary()
    }

    fun onPodcastClick(iTunesId: Long?) {
        if (iTunesId == null) return
        navigate(
            NavCommand.Navigate(
                router = Navigator.Router.ROOT,
                route = NavRoutes.PodcastDetails.getNavGraphRoute(iTunesId = iTunesId)
            )
        )
    }

    private fun getLibrary() {
        viewModelScope.launch {
            getLibrary.libraryState.collect { podcasts ->
                viewModelState.update {
                    LibraryViewModelState(
                        podcasts = podcasts
                    )
                }
            }
        }
        viewModelScope.launch {
            getLibrary.update()
        }
    }
}