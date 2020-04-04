package com.example.orangecast.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.orangecast.entity.ViewEvent
import io.reactivex.disposables.CompositeDisposable


abstract class BaseFragment: Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    protected fun onBackPressed() {
        activity?.onBackPressed()
    }

    protected fun LiveData<ViewEvent>.subscribeToEvent() {
        this.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ViewEvent.Progress<Any> -> onProgress(it)
                is ViewEvent.Error<Any> -> onError(it)
                is ViewEvent.Data<Any> -> onData(it)
            }
        })
    }

    abstract fun onProgress(event: ViewEvent.Progress<Any>)

    abstract fun onError(event: ViewEvent.Error<Any>)

    abstract fun onData(event: ViewEvent.Data<Any>)

    abstract fun inject()

    abstract fun initView()
}