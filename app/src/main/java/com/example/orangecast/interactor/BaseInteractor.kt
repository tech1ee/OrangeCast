package com.example.orangecast.interactor

import com.example.orangecast.data.Parameters

abstract class BaseInteractor {

    protected val baseParameters = mutableMapOf<String, String>().apply {
        this[Parameters.Search.Key.TERM] = Parameters.Search.Value.PODCAST
        this[Parameters.Search.Key.MEDIA] = Parameters.Search.Value.PODCAST
    }

}