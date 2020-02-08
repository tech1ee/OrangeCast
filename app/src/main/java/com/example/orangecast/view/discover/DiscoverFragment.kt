package com.example.orangecast.view.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.App
import com.example.orangecast.BaseFragment
import com.example.orangecast.R
import com.example.orangecast.data.ArtistsByGenre
import kotlinx.android.synthetic.main.fragment_discover.*
import javax.inject.Inject

class DiscoverFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: DiscoverViewModel
    private var adapter = DiscoverAdapter()

    override fun inject() {
        App.appComponent(context)?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        viewModel.getDiscoverLiveData().subscribeToEvent()
        viewModel.discover()
    }

    override fun showData(data: Any?) {
        adapter.setList(data as List<ArtistsByGenre>)
    }
}
