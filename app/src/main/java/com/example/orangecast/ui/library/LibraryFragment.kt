package com.example.orangecast.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.orangecast.App
import com.example.orangecast.databinding.FragmentLibraryBinding
import com.example.orangecast.ui.ViewEvent
import com.example.orangecast.ui.BaseFragment
import com.example.orangecast.ui.snackbar
import javax.inject.Inject

class LibraryFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: LibraryViewModel
    private lateinit var binding: FragmentLibraryBinding
    private val adapter = LibraryAdapter { gotoChannelDetails(it) }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryBinding.inflate(inflater)
        return binding.root
    }

    override fun inject() {
        App.appComponent(context)
                ?.inject(this)
    }

    override fun initView() {
        binding.artistsRv.layoutManager = GridLayoutManager(context, 3)
        binding.artistsRv.adapter = adapter
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.libraryLiveData().observe(viewLifecycleOwner, Observer { onEvent(it) })
        viewModel.getAllSubscriptions()
    }

    private fun onEvent(event: LibraryViewEvent) {
        when (event) {
            is LibraryViewEvent.Data -> showArtistsList(event.subscriptions.list)
        }
    }

    private fun onProgress(event: ViewEvent.Progress) {

    }

    private fun onError(event: ViewEvent.Error) {
        snackbar(binding.root, event.message)
    }

    private fun showArtistsList(list: List<Artist>) {
        if (list.isNullOrEmpty()) showAddSubscriptionsButton()
        else adapter.setList(list)
    }

    private fun showAddSubscriptionsButton() {
        binding.addSubscriptionsButton.setOnClickListener { gotoDiscover() }
        binding.addSubscriptionsLayout.visibility = View.VISIBLE
    }

    private fun gotoChannelDetails(item: Artist) {
        val artistFeedUrl = item.feedUrl ?: return
        val action = LibraryFragmentDirections.artistScreen(artistFeedUrl)
        findNavController().navigate(action)
    }

    private fun gotoDiscover() {
        val action = LibraryFragmentDirections.discoverScreen()
        findNavController().navigate(action)
    }
}