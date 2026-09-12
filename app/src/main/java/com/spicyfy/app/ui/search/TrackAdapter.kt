package com.spicyfy.app.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.spicyfy.app.R
import com.spicyfy.app.data.model.Track

class TrackAdapter(
    private val onTrackClick: (Track) -> Unit
) : ListAdapter<Track, TrackAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val cover: ImageView = itemView.findViewById(R.id.result_cover)
        val title: android.widget.TextView = itemView.findViewById(R.id.result_title)
        val artist: android.widget.TextView = itemView.findViewById(R.id.result_artist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = getItem(position)
        holder.title.text = track.title
        holder.artist.text = track.artist
        holder.cover.load(track.coverUrl) {
            crossfade(true)
            placeholder(R.drawable.gradient_card_small)
            error(R.drawable.gradient_card_small)
        }
        holder.itemView.setOnClickListener { onTrackClick(track) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Track>() {
            override fun areItemsTheSame(oldItem: Track, newItem: Track) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Track, newItem: Track) = oldItem == newItem
        }
    }
}
