package com.spicyfy.app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.spicyfy.app.R
import com.spicyfy.app.data.model.Track

class HomeTrackAdapter(
    private val tracks: List<Track>,
    private val onTrackClick: (Int) -> Unit
) : RecyclerView.Adapter<HomeTrackAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: android.widget.ImageView = itemView.findViewById(R.id.track_cover)
        val title: TextView = itemView.findViewById(R.id.track_title)
        val subtitle: TextView = itemView.findViewById(R.id.track_subtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = tracks[position]
        holder.title.text = track.title
        holder.subtitle.text = track.artist
        holder.cover.load(track.coverUrl) {
            crossfade(true)
            placeholder(R.drawable.gradient_card_2)
            error(R.drawable.gradient_card_2)
        }
        holder.itemView.setOnClickListener { onTrackClick(position) }
    }

    override fun getItemCount(): Int = tracks.size
}
