package com.example.orangecast.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


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
                is ViewEvent.Progress<*> -> onProgress(it)
                is ViewEvent.Error<*> -> onError(it)
                is ViewEvent.Data<*> -> onData(it)
            }
        })
    }

    abstract fun onProgress(event: ViewEvent.Progress<*>)

    abstract fun onError(event: ViewEvent.Error<*>)

    abstract fun onData(event: ViewEvent.Data<*>)

    abstract fun inject()

    abstract fun initView()
}