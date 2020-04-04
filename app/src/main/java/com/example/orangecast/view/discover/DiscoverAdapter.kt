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
import com.example.orangecast.entity.ArtistsByGenre
import com.example.orangecast.entity.Channel
import com.squareup.picasso.Picasso


class DiscoverAdapter(
    private val onItemClicked: (Channel) -> Unit
) : RecyclerView.Adapter<DiscoverAdapter.ViewHolder>() {

    private var list = listOf<ArtistsByGenre>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHorizontalListGenreTitleBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    fun setList(list: List<ArtistsByGenre>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemHorizontalBinding: ItemHorizontalListGenreTitleBinding) : RecyclerView.ViewHolder(itemHorizontalBinding.root) {

        private val genreTitle = itemHorizontalBinding.genreTitle
        private val genresList = itemHorizontalBinding.genreList

        fun bind(position: Int) {
            val item = list[position]

            genreTitle.text = item.title
            genresList.layoutManager = LinearLayoutManager(
                itemView.context, LinearLayoutManager.HORIZONTAL, false
            )
            genresList.adapter = ArtistsAdapter(item.list.toList())

            val shader: Shader = LinearGradient(
                0f, 0f, genreTitle.width.toFloat(), 0f, Color.parseColor("#FFC328"),
                Color.parseColor("#FF3D00"), Shader.TileMode.CLAMP
            )
            genreTitle.paint?.shader = shader
        }
    }

    private inner class ArtistsAdapter(private val artists: List<Channel>
    ) : RecyclerView.Adapter<ArtistsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val itemBinding = ItemAuthorBinding.inflate(inflater, parent, false)
            return ViewHolder(itemBinding)
        }

        override fun getItemCount() = artists.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

        inner class ViewHolder(itemBinding: ItemAuthorBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {

            private val authorPhoto = itemBinding.authorImage
            private val authorTitle = itemBinding.authorTitle
            private val authorName = itemBinding.authorName

            fun bind(position: Int) {
                val item = artists[position]

                Picasso.get().load(item.artworkUrl100).into(authorPhoto)

                authorTitle.text = item.trackName
                authorName.text = item.artistName

                itemView.setOnClickListener { onItemClicked(item) }
            }
        }
    }
}