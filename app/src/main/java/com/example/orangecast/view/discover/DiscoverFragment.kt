package com.example.orangecast.view.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.App
import com.example.orangecast.view.BaseFragment
import com.example.orangecast.R
import com.example.orangecast.entity.MediaItem
import com.example.orangecast.entity.ViewEvent
import kotlinx.android.synthetic.main.fragment_discover.*
import javax.inject.Inject

class DiscoverFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: DiscoverViewModel
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
        list_rv?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        list_rv?.adapter = adapter

        initSearch()
        viewModel.getEventLiveData().subscribeToEvent()
        viewModel.discover()
    }

    override fun <T> onProgress(event: ViewEvent.Progress<T>) {
        TODO("Not yet implemented")
    }

    override fun <T> onError(event: ViewEvent.Error<T>) {
        TODO("Not yet implemented")
    }

    override fun <T, D> onData(event: ViewEvent.Data<T, D>) {
        TODO("Not yet implemented")
    }

//    override fun showData(data: Any?) {
//        adapter.setList(data as List<ArtistsByGenre>)
//    }

    private fun gotoChannelDetails(item: MediaItem) {
        val artistFeedUrl = item.feedUrl ?: return
        val action = DiscoverFragmentDirections.gotoChannelDetails(artistFeedUrl)
        findNavController().navigate(action)
    }
}
