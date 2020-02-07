package com.example.orangecast.view

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.R
import com.example.orangecast.data.ArtistsByGenre
import com.example.orangecast.data.MediaItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_author.view.*
import kotlinx.android.synthetic.main.item_horizontal_list_genre_title.view.*


class GenresAdapter : RecyclerView.Adapter<GenresAdapter.ViewHolder>() {

    private var list = listOf<ArtistsByGenre>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_horizontal_list_genre_title, parent, false)
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    fun setList(list: List<ArtistsByGenre>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val genreTitle = view.genre_title
        private val genresList = view.genre_list

        fun bind(position: Int) {
            val item = list[position]

            genreTitle?.text = item.title
            genresList?.layoutManager = LinearLayoutManager(itemView.context,
                LinearLayoutManager.HORIZONTAL, false)
            genresList?.adapter = ArtistsAdapter(item.list.toList())

            val shader: Shader = LinearGradient(
                0f, 0f, genreTitle.width.toFloat(),0f,
                Color.parseColor("#FFC328"), Color.parseColor("#FF3D00"), Shader.TileMode.CLAMP
            )
            genreTitle?.paint?.shader = shader
        }
    }

    private inner class ArtistsAdapter(
        private val artists: List<MediaItem>
    ) : RecyclerView.Adapter<ArtistsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_author, parent, false))
        }

        override fun getItemCount() = artists.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            private val authorPhoto = view.author_image
            private val authorTitle = view.author_title
            private val authorName = view.author_name

            fun bind(position: Int) {
                val item = artists[position]
                Picasso.get().load(item.artworkUrl100)
                    .into(authorPhoto)

                authorTitle?.text = item.trackName
                authorName?.text = item.artistName
            }
        }
    }
}