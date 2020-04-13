package com.example.orangecast.view.episodes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.R
import com.example.orangecast.databinding.ItemEpisodeBinding
import com.example.orangecast.entity.Episode
import kotlinx.android.synthetic.main.item_episode.view.*

class EpisodesAdapter(
    private val onItemClicked: (Episode) -> Unit
): RecyclerView.Adapter<EpisodesAdapter.ViewHolder>() {

    private var list = arrayListOf<Episode>()

    fun setList(list: List<Episode>) {
        this.list = list as ArrayList<Episode>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemEpisodeBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    inner class ViewHolder(binding: ItemEpisodeBinding): RecyclerView.ViewHolder(binding.root) {

        private val episodeNumber = binding.episodeNumber
        private val publishingDate = binding.publishingDate
        private val episodeTitle = binding.episodeTitle
        private val episodeDuration = binding.episodeDuration
        private val listeningProgressView = binding.listeningProgressView
        private val listeningProgress = binding.listeningProgress
        private val playButton = binding.playButton

        fun bind(position: Int) {
            val item = list[position]

            episodeTitle.text = item.title
            episodeNumber.text = item.episodeNumber
            episodeDuration.text = item.duration
        }
    }
}