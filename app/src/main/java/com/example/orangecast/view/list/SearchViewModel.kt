package com.example.orangecast.view.list

import androidx.lifecycle.MutableLiveData
import com.example.orangecast.BaseViewModel
import com.example.orangecast.data.Parameters
import com.example.orangecast.data.SearchResult
import com.example.orangecast.network.Event
import com.example.orangecast.network.repository.Repository
import javax.inject.Inject

class SearchViewModel : BaseViewModel() {

    @Inject
    lateinit var repository: Repository
    var searchingText: String? = null

    private val parameters = mutableMapOf<String, String>()
    private val searchResult = MutableLiveData<SearchResult>()

    fun getSearchResult(): MutableLiveData<SearchResult> = searchResult

    fun search() {
        checkBaseParameters()
        startSearching()
    }

    private fun startSearching() {
        disposable.add(
            repository.search(parameters)
                .subscribe { event ->
                        manageEvents(event)
                    when (event) {
                        is Event.Success -> {
                            searchResult.apply { this.value = event.data }
                        }
                    }
                    }
        )
    }

    private fun checkBaseParameters() {
        if (!parameters.containsValue(Parameters.Search.Value.PODCAST)) {
            parameters[Parameters.Search.Key.MEDIA] = Parameters.Search.Value.PODCAST
        }
        parameters[Parameters.Search.Key.TERM] = searchingText ?: ""
    }
}