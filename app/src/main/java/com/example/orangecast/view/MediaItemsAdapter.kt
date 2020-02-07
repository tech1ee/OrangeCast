package com.example.orangecast.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.R
import com.example.orangecast.data.MediaItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_media_item.view.*

class MediaItemsAdapter: RecyclerView.Adapter<MediaItemsAdapter.ViewHolder>() {

    private var items = listOf<MediaItem?>()

    fun setItems(items: List<MediaItem?>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_media_item, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val icon = view.item_icon
        private val title = view.item_title
        private val genre = view.item_genre

        fun bind(position: Int) {
            val mediaItem = items[position]
            Picasso.get().load(mediaItem?.artworkUrl100).into(icon)
            title?.text = mediaItem?.artistName
            genre?.text = mediaItem?.primaryGenreName
        }
    }
}