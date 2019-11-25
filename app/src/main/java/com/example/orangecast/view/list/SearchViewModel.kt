package com.example.orangecast.view.list

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.orangecast.data.Parameters
import com.example.orangecast.data.SearchResult
import com.example.orangecast.network.repository.Repository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SearchViewModel : ViewModel() {

    @Inject
    lateinit var repository: Repository
    var searchingText: String? = null

    private val disposable = CompositeDisposable()
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
                .subscribe(
                    {
                    Log.e("SUCCESS", it.toString())
                },
                    {
Log.e("ERROR", it.localizedMessage)
                    })
        )
    }

    private fun checkBaseParameters() {
        if (!parameters.containsValue(Parameters.Search.Value.PODCAST)) {
            parameters[Parameters.Search.Key.MEDIA] = Parameters.Search.Value.PODCAST
        }
        parameters[Parameters.Search.Key.TERM] = searchingText ?: ""
    }
}