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
import com.example.orangecast.entity.ArtistsByGenre
import com.example.orangecast.entity.Episode
import com.example.orangecast.entity.EpisodeList
import com.example.orangecast.entity.ViewEvent
import com.example.orangecast.view.BaseFragment
import com.example.orangecast.view.episodes.EpisodesAdapter
import com.example.orangecast.view.snackbar
import kotlinx.android.synthetic.main.fragment_channel_details.*
import javax.inject.Inject

class ChannelDetailsFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: ChannelDetailsViewModel
    private val args: ChannelDetailsFragmentArgs by navArgs()
    private val episodesAdapter = EpisodesAdapter(object : EpisodesAdapter.Listener {

    })

    override fun inject() {
        App.appComponent(context)?.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_channel_details, container, false)
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

    override fun <T> onProgress(event: ViewEvent.Progress<T>) {

    }

    override fun <T> onError(event: ViewEvent.Error<T>) {
        snackbar(root_layout, event.message)
    }

    override fun <T> onData(event: ViewEvent.Data<T>) {
        when (event) {
             -> episodesAdapter.setList(event.data)
        }
    }

}