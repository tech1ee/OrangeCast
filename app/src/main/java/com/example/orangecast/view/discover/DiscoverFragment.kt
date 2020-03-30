package com.example.orangecast.view.discover

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.App
import com.example.orangecast.view.BaseFragment
import com.example.orangecast.R
import com.example.orangecast.data.ArtistsByGenre
import com.example.orangecast.data.MediaItem
import com.example.orangecast.view.channeldetails.ChannelDetailsFragment
import com.example.orangecast.view.channeldetails.ChannelDetailsFragmentArgs
import io.reactivex.Completable
import kotlinx.android.synthetic.main.fragment_discover.*
import kotlinx.android.synthetic.main.view_logo.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DiscoverFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: DiscoverViewModel
    private var splashScreen: Dialog? = null
    private var adapter = DiscoverAdapter(object : DiscoverAdapter.Listener {
        override fun onItemClicked(item: MediaItem) {
            gotoChannelDetails(item)
        }
    })

    override fun inject() {
        App.appComponent(context)?.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    private fun initSearch() {
        search_view?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return if (query != null) {
                    viewModel.search()
                    true
                } else false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return if (newText != null) {
                    viewModel.searchText = newText
                    true
                } else false
            }

        })
    }

    override fun initView() {
        showSplash()
        list_rv?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        list_rv?.adapter = adapter

        initSearch()
        viewModel.getEventLiveData().subscribeToEvent()
        viewModel.discover()
    }

    override fun showData(data: Any?) {
        adapter.setList(data as List<ArtistsByGenre>)
        disposable.add(Completable.timer(2, TimeUnit.SECONDS).subscribe { hideSplash() })
    }

    private fun showSplash() {
        enableBackButton(false)
        val rotate = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.repeatCount = Animation.INFINITE
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()

        splashScreen = Dialog(context!!, R.style.DialogFragmentFullscreenTheme)
        splashScreen?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        splashScreen?.setContentView(R.layout.fragment_splash)
        splashScreen?.logo_background?.startAnimation(rotate)
        splashScreen?.show()
    }

    private fun gotoChannelDetails(item: MediaItem) {
        val artistFeedUrl = item.feedUrl ?: return
        val action = DiscoverFragmentDirections.gotoChannelDetails(artistFeedUrl)
        findNavController().navigate(action)
    }

    private fun hideSplash() {
        splashScreen?.dismiss()
        enableBackButton(true)
    }
}
