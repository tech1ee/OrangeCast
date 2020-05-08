package com.example.orangecast.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.App
import com.example.orangecast.R
import com.example.orangecast.databinding.FragmentDiscoverBinding
import com.example.orangecast.ui.BaseFragment
import com.example.orangecast.entity.Artist
import com.example.orangecast.entity.Artists
import com.example.orangecast.entity.ViewEvent
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
        viewModel.getEventLiveData().subscribeToEvent()
        viewModel.discover()
    }

    override fun onProgress(event: ViewEvent.Progress<*>) {
        binding.listProgress.root.visibility = if (event.inProgress) View.VISIBLE else View.GONE
        if (!event.inProgress) binding.swipeRefreshLayout.isRefreshing  = false
    }

    override fun onError(event: ViewEvent.Error<*>) {
        snackbar(binding.rootLayout, event.message)
    }

    override fun onData(event: ViewEvent.Data<*>) {
        when (event.data) {
            is List<*> -> adapter.setList(event.data as List<Artists>)
        }
    }

    private fun gotoChannelDetails(item: Artist) {
        val artistFeedUrl = item.feedUrl ?: return
        val action = DiscoverFragmentDirections.channelDetails(artistFeedUrl)
        findNavController().navigate(action)
    }
}
