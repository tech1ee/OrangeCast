package com.example.orangecast.ui.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.App
import com.example.orangecast.R
import com.example.orangecast.databinding.FragmentArtistBinding
import com.example.orangecast.entity.Artist
import com.example.orangecast.entity.Feed
import com.example.orangecast.ui.ViewEvent
import com.example.orangecast.ui.BaseFragment
import com.example.orangecast.ui.episodes.EpisodesAdapter
import com.example.orangecast.ui.snackbar
import com.example.orangecast.ui.utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_artist.*
import kotlinx.android.synthetic.main.view_artist_details_top.view.*
import javax.inject.Inject

class ArtistFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: ArtistViewModel
    private lateinit var binding: FragmentArtistBinding
    private val args: ArtistFragmentArgs by navArgs()
    private val episodesAdapter = EpisodesAdapter { episode ->

    }

    override fun inject() {
        App.appComponent(context)?.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArtistBinding.inflate(inflater)
        return binding.root
    }

    override fun initView() {
        initButtons()
        initRefreshing()
        initEpisodesList()

        subscribeToViewModel()
    }

    private fun initButtons() {
        binding.toolbarView.backButton.setOnClickListener { onBackPressed() }
    }

    private fun initRefreshing() {
        if (context == null) return
        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(context!!, R.color.colorGradientDarkEnd)
        )
        binding.swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(context!!, R.color.colorAccent)
        )
        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.getArtistDetails() }
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun initEpisodesList() {
        binding.episodesListRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = episodesAdapter
        }
    }

    private fun subscribeToViewModel() {
        viewModel.infoLiveData().observe(viewLifecycleOwner, Observer { onEvent(it) })
        viewModel.feedLiveData().observe(viewLifecycleOwner, Observer { onEvent(it) })
        viewModel.setFeedUrl(args.artistFeedUrl)
        viewModel.getArtistDetails()
    }

    private fun onEvent(event: ArtistViewEvent) {
        when (event) {
            is ArtistViewEvent.ArtistInfo.Data -> showArtistDetails(event.artist)
            is ArtistViewEvent.ArtistInfo.Subscribed -> initSubscribeButton(event.isSubscribed)
            is ArtistViewEvent.ArtistFeed.Data -> showChannelFeed(event.feed)
            is ArtistViewEvent.ArtistFeed.Progress -> onProgress(event.inProgress)
        }
    }

    private fun onProgress(inProgress: Boolean) {
        binding.channelListProgress.root.visibility = if (inProgress) View.VISIBLE else View.GONE
        if (!inProgress) binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun onError(event: ViewEvent.Error) {
        snackbar(binding.rootContainer, event.message)
    }

    private fun showArtistDetails(channel: Artist) {
        Picasso.get().load(channel.artworkUrl100).transform(CircleTransform)
            .placeholder(R.drawable.circle_background).into(binding.headerView.authorImage)
        binding.headerView.authorTitle.text = channel.collectionName
        binding.headerView.authorName.text = channel.artistName
    }

    private fun initSubscribeButton(isSubscribed: Boolean) {
        val context = context ?: return
        binding.headerView.subscribeButtonText.text = if (isSubscribed) getString(R.string.subscribed)
        else getString(R.string.subscribe)
        binding.headerView.subscribeButtonBackground.setImageDrawable(ContextCompat.getDrawable(
            context, if (isSubscribed) R.drawable.button_subscribed else R.drawable.button_subscribe
        ))
        binding.headerView.subscribeButton.setOnClickListener {
            if (isSubscribed) viewModel.unsubscribe() else viewModel.subscribe()
        }
    }

    private fun showChannelFeed(feed: Feed?) {
        header?.author_description?.text = feed?.description
        episodesAdapter.setList(feed?.episodes ?: listOf())
    }

}