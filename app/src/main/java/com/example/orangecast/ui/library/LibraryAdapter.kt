package com.example.orangecast.ui.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.databinding.ItemLibraryBinding
import com.example.orangecast.entity.Artist
import com.squareup.picasso.Picasso

class LibraryAdapter(
    private val onItemClicked: (Artist) -> Unit
): RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

    private var list: List<Artist> = listOf()

    fun setList(list: List<Artist>) {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLibraryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    inner class ViewHolder(binding: ItemLibraryBinding): RecyclerView.ViewHolder(binding.root) {

        private val image = binding.authorImage
        private val title = binding.authorTitle
        private val name = binding.authorName

        fun bind(position: Int) {
            val item = list[position]

            Picasso.get().load(item.artworkUrl100).into(image)
            title.text = item.artistName
            name.text = item.collectionName

            itemView.setOnClickListener { onItemClicked(item) }
        }

    }
}