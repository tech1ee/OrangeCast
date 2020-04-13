package com.example.orangecast.view.channeldetails

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.databinding.ViewChannelDetailsTopBinding

class ChannelDetailsListScrollListener(binding: ViewChannelDetailsTopBinding
) : RecyclerView.OnScrollListener() {

    private val topView = binding.root
    private val image = binding.authorImage

    private var topOriginalHeight: Float = -1f
    private var topCollapsedHeight: Float = -1f
    private var minScale: Float = 0.06f
    private var viewSet = false

    private fun setView() {
        topView.viewTreeObserver.addOnGlobalLayoutListener {
            if (viewSet) return@addOnGlobalLayoutListener
            viewSet = true
            topOriginalHeight = topView.measuredHeight.toFloat()
            topCollapsedHeight = topOriginalHeight * minScale
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        setView()
        val topHeight = topView.measuredHeight
        if (dy > 0) {
            // Scrolling up
            if (topHeight > topCollapsedHeight) {

                //--- Shrink top
                val height = topHeight - dy
                topView.layoutParams.height = if (height < topCollapsedHeight) topCollapsedHeight.toInt() else height
                topView.requestLayout()

                //--- Translate up top
                image.translationY = -dy.toFloat()
            }
        } else {
            // Scrolling down
            if (topHeight < topOriginalHeight) {

                //--- Expand top
                val height = topHeight - dy
                topView.layoutParams.height = if (height > topOriginalHeight) topOriginalHeight.toInt() else height
                topView.requestLayout()

                //--- Translate down top
                image.translationY = -dy.toFloat()
            }
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        when (newState) {

        }
    }
}