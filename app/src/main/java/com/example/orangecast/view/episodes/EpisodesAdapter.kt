package com.example.orangecast.view.episodes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.databinding.ItemEpisodeBinding
import com.example.orangecast.entity.Episode

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
        val itemBinding = ItemEpisodeBinding.inflate(inflater, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    inner class ViewHolder(itemBinding: ItemEpisodeBinding): RecyclerView.ViewHolder(itemBinding.root) {

        private val episodeNumber = itemBinding.episodeNumber
        private val publishingDate = itemBinding.publishingDate
        private val episodeTitle = itemBinding.episodeTitle
        private val episodeDuration = itemBinding.episodeDuration
        private val listeningProgressView = itemBinding.listeningProgressView
        private val listeningProgress = itemBinding.listeningProgress
        private val playButton = itemBinding.playButton

        fun bind(position: Int) {
            val item = list[position]

            episodeTitle.text = item.title
            episodeNumber.text = item.episodeNumber
            episodeDuration.text = item.duration
        }
    }
}