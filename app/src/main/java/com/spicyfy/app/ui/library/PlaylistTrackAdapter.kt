package com.spicyfy.app.ui.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.spicyfy.app.R
import com.spicyfy.app.data.model.Track

class PlaylistTrackAdapter(
    private val tracks: List<Track>,
    private val onTrackClick: (Int) -> Unit
) : RecyclerView.Adapter<PlaylistTrackAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val number: TextView = itemView.findViewById(R.id.pt_number)
        val cover: ImageView = itemView.findViewById(R.id.pt_cover)
        val title: TextView = itemView.findViewById(R.id.pt_title)
        val artist: TextView = itemView.findViewById(R.id.pt_artist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist_track, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = tracks[position]
        holder.number.text = (position + 1).toString()
        holder.title.text = track.title
        holder.artist.text = track.artist
        holder.cover.load(track.coverUrl) {
            crossfade(true)
            placeholder(R.drawable.gradient_card_small)
            error(R.drawable.gradient_card_small)
        }
        holder.itemView.setOnClickListener { onTrackClick(position) }
    }

    override fun getItemCount(): Int = tracks.size
}
