package com.example.orangecast.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.R
import com.example.orangecast.entity.Channel
import kotlinx.android.synthetic.main.item_episode.view.*

class MediaItemsAdapter: RecyclerView.Adapter<MediaItemsAdapter.ViewHolder>() {

    private var items = listOf<Channel?>()

    fun setItems(items: List<Channel?>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_episode, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val title = view.episode_title

        fun bind(position: Int) {
            val mediaItem = items[position]
            title?.text = mediaItem?.artistName

        }
    }
}