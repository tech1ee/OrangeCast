package com.example.orangecast.view.discover

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.databinding.ItemAuthorBinding
import com.example.orangecast.databinding.ItemHorizontalListGenreTitleBinding
import com.example.orangecast.entity.Artist
import com.example.orangecast.entity.Artists
import com.squareup.picasso.Picasso


class DiscoverAdapter(
    private val onItemClicked: (Artist) -> Unit
) : RecyclerView.Adapter<DiscoverAdapter.ViewHolder>() {

    private var list = listOf<Artists>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemHorizontalListGenreTitleBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    fun setList(list: List<Artists>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: ItemHorizontalListGenreTitleBinding) : RecyclerView.ViewHolder(binding.root) {

        private val genreTitle = binding.genreTitle
        private val genresList = binding.genreList

        fun bind(position: Int) {
            val item = list[position]

            genreTitle.text = item.title
            genresList.layoutManager = LinearLayoutManager(
                itemView.context, LinearLayoutManager.HORIZONTAL, false
            )
            genresList.adapter = ArtistsAdapter(item.list.toList())

            val shader: Shader = LinearGradient(
                0f, 0f, genreTitle.width.toFloat() ?: 0f, 0f, Color.parseColor("#FFC328"),
                Color.parseColor("#FF3D00"), Shader.TileMode.CLAMP
            )
            genreTitle.paint?.shader = shader
        }
    }

    private inner class ArtistsAdapter(private val artists: List<Artist>
    ) : RecyclerView.Adapter<ArtistsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return ViewHolder(ItemAuthorBinding.inflate(inflater, parent, false))
        }

        override fun getItemCount() = artists.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

        inner class ViewHolder(binding: ItemAuthorBinding) : RecyclerView.ViewHolder(binding.root) {

            private val authorPhoto = binding.authorImage
            private val authorTitle = binding.authorTitle
            private val authorName = binding.authorName

            fun bind(position: Int) {
                val item = artists[position]

                Picasso.get().load(item.artworkUrl100).into(authorPhoto)

                authorTitle.text = item.artistName
                authorName.text = item.collectionName

                itemView.setOnClickListener { onItemClicked(item) }
            }
        }
    }
}