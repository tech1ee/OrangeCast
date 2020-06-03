package com.example.orangecast.ui.discover

import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.App
import com.example.orangecast.R
import com.example.orangecast.databinding.FragmentDiscoverBinding
import com.example.orangecast.ui.BaseFragment
import com.example.orangecast.entity.Artist
import com.example.orangecast.entity.ArtistsGenre
import com.example.orangecast.entity.Genres
import com.example.orangecast.ui.ViewEvent
import com.example.orangecast.ui.snackbar
import kotlinx.android.synthetic.main.fragment_discover.*
import javax.inject.Inject

class DiscoverFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: DiscoverViewModel
    private lateinit var binding: FragmentDiscoverBinding

    private var adapter = DiscoverAdapter { item ->
        gotoChannelDetails(item)
    }

    override fun inject() {
        App.appComponent(context)?.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiscoverBinding.inflate(inflater)
        return binding.root
    }

    private fun initSearch() {
        search_view?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return if (query != null) {

                    true
                } else false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return if (newText != null) {

                    true
                } else false
            }
        })
    }

    private fun initRefreshing() {
        if (context == null) return
        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(context!!, R.color.colorGradientDarkEnd))
        binding.swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))
        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.discover(true) }
        binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun initView() {
        binding.listRv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.listRv.adapter = adapter

        initSearch()
        initRefreshing()
        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        Log.e("DiscoverFragment", "onStart")
    }

    override fun onResume() {
        super.onResume()
        viewModel.discover()
    }

    private fun observeViewModel() {
        viewModel.discoverLiveData().observe(viewLifecycleOwner, Observer { onEvent(it) })
    }

    private fun onEvent(event: DiscoverViewEvent) {
        when (event) {
            is DiscoverViewEvent.Progress -> onProgress(event.inProgress)
            is DiscoverViewEvent.Error -> onError(event.message)
            is DiscoverViewEvent.Data -> onData(event.data)
        }
    }

    private fun onProgress(inProgress: Boolean) {
        binding.listProgress.root.visibility = if (inProgress) View.VISIBLE else View.GONE
        if (!inProgress) binding.swipeRefreshLayout.isRefreshing  = false
    }

    private fun onError(message: String) {
        snackbar(binding.rootLayout, message)
    }

    private fun onData(data: Genres) {
        adapter.setList(data.list)
    }

    private fun gotoChannelDetails(item: Artist) {
        val artistFeedUrl = item.feedUrl ?: return
        val action = DiscoverFragmentDirections.artist(artistFeedUrl)
        findNavController().navigate(action)
    }
}
