package com.spicyfy.app.ui.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.spicyfy.app.R
import com.spicyfy.app.data.model.Playlist

class PlaylistAdapter(
    private val onPlaylistClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlaylistAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: ImageView = itemView.findViewById(R.id.playlist_cover)
        val name: TextView = itemView.findViewById(R.id.playlist_name)
        val trackCount: TextView = itemView.findViewById(R.id.playlist_track_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = getItem(position)
        holder.name.text = playlist.name
        holder.trackCount.text = holder.itemView.context.getString(
            R.string.playlist_track_count, playlist.tracks.size
        )
        holder.cover.load(playlist.tracks.firstOrNull()?.coverUrl) {
            crossfade(true)
            placeholder(R.drawable.gradient_card_2)
            error(R.drawable.gradient_card_2)
        }
        holder.itemView.setOnClickListener { onPlaylistClick(playlist) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Playlist>() {
            override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist) = oldItem == newItem
        }
    }
}
