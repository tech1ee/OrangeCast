package com.example.orangecast.ui.discover

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.databinding.ItemArtistBinding
import com.example.orangecast.databinding.ItemHorizontalListGenreTitleBinding
import com.example.orangecast.entity.Artist
import com.example.orangecast.entity.ArtistsGenre
import com.example.orangecast.ui.utils.CircleTransform
import com.squareup.picasso.Picasso


class DiscoverAdapter(private val onItemClicked: (Artist) -> Unit
) : RecyclerView.Adapter<DiscoverAdapter.ViewHolder>() {

    private var list = listOf<ArtistsGenre>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemHorizontalListGenreTitleBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    fun setList(list: List<ArtistsGenre>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: ItemHorizontalListGenreTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val genreTitle = binding.genreTitle
        private val genresList = binding.genreList

        fun bind(position: Int) {
            val item = list[position]

            genreTitle.text = item.genre
            genresList.layoutManager = LinearLayoutManager(
                itemView.context, LinearLayoutManager.HORIZONTAL, false
            )
            genresList.adapter = ArtistsAdapter(item.list.toList())
        }
    }

    private inner class ArtistsAdapter(private val artists: List<Artist>
    ) : RecyclerView.Adapter<ArtistsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return ViewHolder(ItemArtistBinding.inflate(inflater, parent, false))
        }

        override fun getItemCount() = artists.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

        inner class ViewHolder(binding: ItemArtistBinding) : RecyclerView.ViewHolder(binding.root) {

            private val authorPhoto = binding.authorImage
            private val authorTitle = binding.authorTitle
            private val authorName = binding.authorName

            fun bind(position: Int) {
                val item = artists[position]

                Picasso.get()
                    .load(item.artworkUrl100)
                    .transform(CircleTransform)
                    .into(authorPhoto)

                authorTitle.text = item.artistName
                authorName.text = item.collectionName

                authorPhoto.setOnClickListener { onItemClicked(item) }
                itemView.setOnClickListener { onItemClicked(item) }
            }
        }
    }
}