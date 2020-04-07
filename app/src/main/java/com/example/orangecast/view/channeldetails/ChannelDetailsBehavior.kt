package com.example.orangecast.view.channeldetails

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.example.orangecast.R
import com.example.orangecast.databinding.FragmentChannelDetailsBinding
import com.google.android.material.appbar.AppBarLayout

class ChannelDetailsBehavior : CoordinatorLayout.Behavior<AppBarLayout>() {

    private lateinit var container: View
    private lateinit var backButton: View
    private lateinit var shareButton: View
    private lateinit var descriptionBackground: View
    private lateinit var descriptionLayout: View
    private lateinit var authorImageCard: View
    private lateinit var authorTitle: View
    private lateinit var authorName: View
    private lateinit var subscribeButton: View
    private lateinit var filterLayout: View

    private var containerOriginalHeight: Float = -1f
    private var containerCollapsedHeight: Float = -1f
    private var viewSet = false
    private var minScale = 0.6f

    private fun getViews(child: AppBarLayout) {
        if (viewSet) return

        container = child.findViewById(R.id.top_container)
        backButton = child.findViewById(R.id.back_button)
        shareButton = child.findViewById(R.id.share_button)
        descriptionBackground = child.findViewById(R.id.description_background)
        descriptionLayout = child.findViewById(R.id.author_description_scroll)
        authorImageCard = child.findViewById(R.id.author_card)
        authorTitle = child.findViewById(R.id.author_title)
        authorName = child.findViewById(R.id.author_name)
        subscribeButton = child.findViewById(R.id.subscribe_button)
        filterLayout = child.findViewById(R.id.episode_list_filter_layout)
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, directTargetChild: View,
        target: View, axes: Int, type: Int): Boolean {
        getViews(child)
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout,
        target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int,
        type: Int, consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            type, consumed
        )
        getViews(child)

        if (dyConsumed > 0) {

            // scroll up
            if (container.layoutParams.height > containerCollapsedHeight) {

                //--- shrink container
                val height = container.layoutParams.height - dyConsumed
                container.layoutParams.height = if (height < containerCollapsedHeight) containerCollapsedHeight.toInt() else height
                container.requestLayout()

                //--- translate up views
                var translate: Float = (containerOriginalHeight - container.layoutParams.height) / (containerOriginalHeight - containerCollapsedHeight)
                translate *= containerOriginalHeight
                shareButton.translationX = -translate
                descriptionBackground.translationX = -translate
                descriptionLayout.translationX = -translate
                authorImageCard.translationX = -translate
                authorName.translationX = -translate
                subscribeButton.translationX = -translate
                filterLayout.translationX = -translate

                //--- title
                val scale = container.layoutParams.height / containerOriginalHeight
                authorTitle.scaleX = if (scale < minScale) minScale else scale
                authorTitle.scaleY = authorTitle.scaleX
            }
        } else if (dyConsumed < 0) {

            // scroll down
            if (container.layoutParams.height < containerOriginalHeight) {

                //--- expand container
                val height = container.layoutParams.height - dyUnconsumed
                container.layoutParams.height = if (height > containerOriginalHeight) containerOriginalHeight.toInt() else height
                container.requestLayout()

                //--- translate down views
                var translate: Float = (containerOriginalHeight - container.layoutParams.height) / (containerOriginalHeight - containerCollapsedHeight)
                translate *= containerOriginalHeight
                shareButton.translationX = -translate
                descriptionBackground.translationX = -translate
                descriptionLayout.translationX = -translate
                authorImageCard.translationX = -translate
                authorName.translationX = -translate
                subscribeButton.translationX = -translate
                filterLayout.translationX = -translate

                //--- title
                val scale = container.layoutParams.height / containerOriginalHeight
                authorTitle.scaleX = if (scale < minScale) minScale else scale
                authorTitle.scaleY = authorTitle.scaleX
            }
        }
    }

}