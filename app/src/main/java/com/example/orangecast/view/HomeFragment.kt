package com.example.orangecast.view


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
import com.example.orangecast.view.list.MediaItemsAdapter
import com.example.orangecast.view.list.SearchViewModel
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private var searchViewModel: SearchViewModel? = null
    private var adapter = MediaItemsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initSearchViewModel()
    }

    private fun initSearchViewModel() {
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        App.appComponent(context)?.inject(searchViewModel)
        searchViewModel?.getSearchResult()?.observe(viewLifecycleOwner, Observer { event ->
            adapter.setItems(event?.results ?: listOf())
        })
    }

    private fun initView() {
        list_rv?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        list_rv?.adapter = adapter
        search_view?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return if (query != null) {
                    searchViewModel?.search()
                    true
                } else false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return if (newText != null) {
                    searchViewModel?.searchingText = newText
                    true
                } else false
            }

        })
    }
}
