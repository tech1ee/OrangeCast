package com.example.orangecast.view.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.orangecast.data.Parameters
import com.example.orangecast.data.SearchResult
import com.example.orangecast.network.repository.Repository

class SearchViewModel(
    private val repository: Repository
): ViewModel() {

    var searchingText: String? = null

    private val parameters = mutableMapOf<String, String>()
    private val searchResult = MutableLiveData<SearchResult>()

    fun getSearchResult(): MutableLiveData<SearchResult> = searchResult

    fun search() {
        checkBaseParameters()
        startSearching()
    }

    private fun startSearching() {
        searchResult.apply { repository.search(parameters) }
    }

    private fun checkBaseParameters() {
        if (!parameters.containsValue(Parameters.Search.Value.PODCAST)) {
            parameters[Parameters.Search.Key.MEDIA] = Parameters.Search.Value.PODCAST
        }
        parameters[Parameters.Search.Key.TERM] = searchingText ?: ""
    }
}