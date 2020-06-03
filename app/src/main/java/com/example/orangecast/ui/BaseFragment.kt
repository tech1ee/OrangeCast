package com.example.orangecast.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
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

    abstract fun inject()

    abstract fun initView()
}