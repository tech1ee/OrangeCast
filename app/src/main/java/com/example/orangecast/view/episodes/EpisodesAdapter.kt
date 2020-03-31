package com.example.orangecast.view.episodes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.orangecast.R
import com.example.orangecast.entity.Episode
import kotlinx.android.synthetic.main.item_media_item.view.*

class EpisodesAdapter(
    private val listener: Listener
): RecyclerView.Adapter<EpisodesAdapter.ViewHolder>() {

    private var list = arrayListOf<Episode>()

    fun setList(list: List<Episode>) {
        this.list = list as ArrayList<Episode>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_media_item, parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val episodeNumber = view.episode_number
        private val publishingDate = view.publishing_date
        private val episodeTitle = view.episode_title
        private val episodeDuration = view.episode_duration
        private val listeningProgressView = view.listening_progress_view
        private val listeningProgress = view.listening_progress
        private val playButton = view.play_button

        fun bind(position: Int) {
            val item = list[position]

            episodeTitle?.text = item.title
            episodeNumber?.text = item.episodeNumber
            episodeDuration?.text = item.duration
        }
    }

    interface Listener {

    }
}