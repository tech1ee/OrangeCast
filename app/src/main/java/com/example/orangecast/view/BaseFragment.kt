package com.example.orangecast.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.orangecast.R
import com.example.orangecast.entity.Event
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable


abstract class BaseFragment: Fragment() {

    protected val disposable = CompositeDisposable()
    private var progressView: View? = null
    private var isProgressShowingRequired = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject()
        val rootView: ViewGroup? = activity?.findViewById(android.R.id.content)
        progressView = LayoutInflater.from(context)
            .inflate(R.layout.view_progress_fullscreen, rootView, false)
        progressView?.visibility = View.GONE
        rootView?.addView(progressView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    protected fun disableProgress() {
        isProgressShowingRequired = false
    }

    protected fun enableBackButton(enabled: Boolean) {
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(enabled) {
                override fun handleOnBackPressed() {
                    if (enabled) requireActivity().onBackPressed()
                }
            })
    }

    protected fun LiveData<Event>.subscribeToEvent() {
        this.observe(viewLifecycleOwner, Observer { onEvent(it) })
    }

    private fun onEvent(event: Event) {
        when (event) {
            is Event.Progress -> showProgress(event.inProgress)
            is Event.Error -> showError(event.message)
            is Event.Data<*> -> showData(event.data)
        }
    }

    abstract fun inject()

    abstract fun initView()

    open fun showData(data: Any?) {}

    private fun showProgress(show: Boolean) {
        if (show && isProgressShowingRequired) progressView?.visibility = View.VISIBLE
        else progressView?.visibility = View.GONE
    }

    private fun showError(message: String) {
        if (view != null) Snackbar.make(view!!, message, Snackbar.LENGTH_LONG)
    }
}