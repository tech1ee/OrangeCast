package com.example.orangecast.view.channeldetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.App
import com.example.orangecast.R
import com.example.orangecast.entity.Channel
import com.example.orangecast.entity.Feed
import com.example.orangecast.entity.ViewEvent
import com.example.orangecast.view.BaseFragment
import com.example.orangecast.view.episodes.EpisodesAdapter
import com.example.orangecast.view.snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_channel_details.*
import kotlinx.android.synthetic.main.view_channel_details_top.view.*
import javax.inject.Inject

class ChannelDetailsFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: ChannelDetailsViewModel
    private val args: ChannelDetailsFragmentArgs by navArgs()
    private val episodesAdapter = EpisodesAdapter { episode ->

    }

    override fun inject() {
        App.appComponent(context)?.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_channel_details, container, false)
    }

    override fun initView() {
        initButtons()

        episodes_list_rv?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        episodes_list_rv?.adapter = episodesAdapter

        viewModel.getEventLiveData().subscribeToEvent()
        viewModel.getChannelDetails(args.artistFeedUrl)
    }

    private fun initButtons() {
        header?.back_button?.setOnClickListener { onBackPressed() }
    }

    override fun onProgress(event: ViewEvent.Progress<*>) {
        channel_list_progress?.visibility = if (event.inProgress) View.VISIBLE else View.GONE
    }

    override fun onError(event: ViewEvent.Error<*>) {
        snackbar(motion_layout, event.message)
    }

    override fun onData(event: ViewEvent.Data<*>) {
        when (event.data) {
            is Channel -> {
                showChannelDetails(event.data)
                showChannelFeed(event.data.feed)
            }
        }
    }

    private fun showChannelDetails(channel: Channel) {
        Picasso.get().load(channel.artworkUrl100).into(header?.author_image)
        header?.author_title?.text = channel.collectionName
        header?.author_name?.text = channel.artistName
    }

    private fun showChannelFeed(feed: Feed?) {
        header?.author_description?.text = feed?.description
        episodesAdapter.setList(feed?.episodes ?: listOf())
    }

}