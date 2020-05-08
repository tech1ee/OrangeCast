package com.example.orangecast.ui

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.snackbar(view: View?, message: String) {
    if (view != null) Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}