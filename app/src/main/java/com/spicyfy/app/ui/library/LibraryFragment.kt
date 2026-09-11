package com.spicyfy.app.ui.library

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.spicyfy.app.R
import com.spicyfy.app.databinding.FragmentLibraryBinding
import com.spicyfy.app.player.PlayerViewModel

class LibraryFragment : Fragment(R.layout.fragment_library) {

    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = FragmentLibraryBinding.bind(view)

        val adapter = PlaylistAdapter { playlist ->
            if (playlist.tracks.isNotEmpty()) {
                playerViewModel.play(playlist.tracks, startIndex = 0)
            }
        }
        b.libraryPlaylists.layoutManager = LinearLayoutManager(requireContext())
        b.libraryPlaylists.adapter = adapter

        b.libraryNewPlaylist.setOnClickListener { showCreatePlaylistDialog() }

        libraryViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            adapter.submitList(playlists)
            b.libraryEmptyText.visibility = if (playlists.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        libraryViewModel.refresh()
    }

    private fun showCreatePlaylistDialog() {
        val input = EditText(requireContext()).apply {
            hint = getString(R.string.playlist_name_hint)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_playlist)
            .setView(input)
            .setPositiveButton(R.string.create) { _, _ ->
                libraryViewModel.createPlaylist(input.text.toString())
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
