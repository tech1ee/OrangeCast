package com.example.orangecast.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class GradientTextView: AppCompatTextView {

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            paint?.shader = LinearGradient(
                0f, 0f, width.toFloat(), 0f, Color.parseColor("#FFC328"),
                Color.parseColor("#FF3D00"), Shader.TileMode.CLAMP
            )
        }
    }
}