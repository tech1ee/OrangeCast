package com.example.orangecast

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.orangecast.network.Event
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment: Fragment() {

    protected val disposable = CompositeDisposable()
    private var progressAlert: AlertDialog.Builder? = null
    private var progressDialog: Dialog? = null
    private var isProgressShowingRequired = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject()
        progressAlert = AlertDialog.Builder(context!!)
        progressAlert?.setView(R.layout.view_progress_fullscreen)
        progressDialog = progressAlert?.create()
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
        if (show && isProgressShowingRequired) progressDialog?.show()
        else progressDialog?.dismiss()
    }

    private fun showError(message: String) {
        if (view != null) Snackbar.make(view!!, message, Snackbar.LENGTH_LONG)
    }
}