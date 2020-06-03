package com.example.orangecast.ui

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel : ViewModel() {

    protected val disposable = CompositeDisposable()

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}