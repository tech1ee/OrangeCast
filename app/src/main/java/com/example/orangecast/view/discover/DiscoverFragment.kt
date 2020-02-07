package com.example.orangecast.view.discover

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.App
import com.example.orangecast.R
import com.example.orangecast.data.ArtistsByGenre
import com.example.orangecast.network.Event
import com.example.orangecast.view.GenresAdapter
import com.example.orangecast.view.MediaItemsAdapter
import com.example.orangecast.view.SearchViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_discover.*
import javax.inject.Inject

class DiscoverFragment : Fragment() {

    private var searchViewModel: SearchViewModel? = null
    @Inject
    lateinit var discoverViewModel: DiscoverViewModel
    private var adapter = GenresAdapter()

    override fun onAttach(context: Context) {
        App.appComponent(context)?.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        discoverViewModel.getDiscoverLiveData().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
        })
        discoverViewModel.discover()
//        initSearch()
    }

//    private fun initSearch() {
//        searchViewModel?.getSearchResult()?.observe(viewLifecycleOwner, Observer { event ->
//            adapter.setItems(event?.results ?: listOf())
//        })
//    search_view?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//        override fun onQueryTextSubmit(query: String?): Boolean {
//            return if (query != null) {
//                searchViewModel?.search()
//                true
//            } else false
//        }
//
//        override fun onQueryTextChange(newText: String?): Boolean {
//            return if (newText != null) {
//                searchViewModel?.searchingText = newText
//                true
//            } else false
//        }
//
//    })
//    }

    private fun initView() {
        list_rv?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        list_rv?.adapter = adapter
    }
}
