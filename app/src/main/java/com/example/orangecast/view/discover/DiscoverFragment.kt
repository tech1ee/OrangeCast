package com.example.orangecast.view.discover

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.App
import com.example.orangecast.R
import kotlinx.android.synthetic.main.fragment_discover.*
import javax.inject.Inject

class DiscoverFragment : Fragment() {

    @Inject
    lateinit var viewModel: DiscoverViewModel
    private var adapter = DiscoverAdapter()

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
        initSearch()
        viewModel.getDiscoverLiveData().observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
        })
        viewModel.discover()
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

    private fun initView() {
        list_rv?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        list_rv?.adapter = adapter
    }
}
