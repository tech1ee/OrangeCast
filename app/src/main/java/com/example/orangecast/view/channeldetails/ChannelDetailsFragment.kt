package com.example.orangecast.view.channeldetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.App
import com.example.orangecast.databinding.FragmentChannelDetailsBinding
import com.example.orangecast.entity.Channel
import com.example.orangecast.entity.Episode
import com.example.orangecast.entity.Feed
import com.example.orangecast.entity.ViewEvent
import com.example.orangecast.view.BaseFragment
import com.example.orangecast.view.episodes.EpisodesAdapter
import com.example.orangecast.view.snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_channel_details.*
import javax.inject.Inject

class ChannelDetailsFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: ChannelDetailsViewModel
    private var binding: FragmentChannelDetailsBinding? = null
    private val args: ChannelDetailsFragmentArgs by navArgs()
    private val episodesAdapter = EpisodesAdapter { episode ->

    }

    override fun inject() {
        App.appComponent(context)?.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChannelDetailsBinding.inflate(inflater)
        return binding?.root
    }

    override fun initView() {
        viewModel.getEventLiveData().subscribeToEvent()
        viewModel.getArtistDetails(args.artistFeedUrl)

        initButtons()

        episodes_list_rv?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        episodes_list_rv?.adapter = episodesAdapter
    }

    private fun initButtons() {
        back_button?.setOnClickListener { onBackPressed() }
    }

    override fun onProgress(event: ViewEvent.Progress<*>) {
        binding?.listProgress?.root?.visibility = if (event.inProgress) View.VISIBLE else View.GONE
    }

    override fun onError(event: ViewEvent.Error<*>) {
        snackbar(root_layout, event.message)
    }

    override fun onData(event: ViewEvent.Data<*>) {
        when (event.data) {
            is Channel -> showChannelDetails(event.data)
            is Feed -> showChannelFeed(event.data)
        }
    }

    private fun showChannelDetails(channel: Channel) {
        Picasso.get().load(channel.artworkUrl100).into(binding?.authorImage)
        binding?.authorTitle?.text = channel.collectionName
        binding?.authorName?.text = channel.artistName
        binding?.authorDescription?.text = channel.artistName
    }

    private fun showChannelFeed(feed: Feed) {
        binding?.authorDescription?.text = feed.description
        episodesAdapter.setList(feed.episodes ?: listOf())
    }

}