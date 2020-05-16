package com.example.orangecast.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.orangecast.App
import com.example.orangecast.databinding.FragmentLibraryBinding
import com.example.orangecast.entity.Artist
import com.example.orangecast.ui.ViewEvent
import com.example.orangecast.ui.BaseFragment
import javax.inject.Inject

class LibraryFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: LibraryViewModel
    private lateinit var binding: FragmentLibraryBinding
    private val adapter = LibraryAdapter { gotoChannelDetails(it) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryBinding.inflate(inflater)
        return binding.root
    }

    override fun inject() {
        App.appComponent(context)?.inject(this)
    }

    override fun initView() {
        binding.artistsRv.layoutManager = GridLayoutManager(context, 3)
        viewModel.getAllSubscriptions()
    }

    override fun onData(event: ViewEvent.Data<*>) {
        when (event.data) {
            is List<*> -> showArtistsList(event.data as List<Artist>)
        }
    }

    override fun onProgress(event: ViewEvent.Progress<*>) {

    }

    override fun onError(event: ViewEvent.Error<*>) {

    }

    private fun showArtistsList(list: List<Artist>) {

    }

    private fun gotoChannelDetails(item: Artist) {
        val artistFeedUrl = item.feedUrl ?: return
        val action = LibraryFragmentDirections.channelDetails(artistFeedUrl)
        findNavController().navigate(action)
    }
}