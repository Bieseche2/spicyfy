package com.spicyfy.app.ui.library

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.spicyfy.app.R
import com.spicyfy.app.databinding.FragmentPlaylistDetailBinding
import com.spicyfy.app.player.PlayerViewModel
import com.spicyfy.app.ui.MainActivity

class PlaylistDetailFragment : Fragment(R.layout.fragment_playlist_detail) {

    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()
    private val playlistId: String by lazy { requireArguments().getString(ARG_PLAYLIST_ID)!! }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = FragmentPlaylistDetailBinding.bind(view)

        b.plBack.setOnClickListener { parentFragmentManager.popBackStack() }

        libraryViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            val playlist = playlists.find { it.id == playlistId } ?: return@observe

            b.plName.text = playlist.name
            b.plMeta.text = getString(R.string.playlist_track_count, playlist.tracks.size)
            b.plCover.load(playlist.tracks.firstOrNull()?.coverUrl) {
                crossfade(true)
                placeholder(R.drawable.gradient_card_1)
                error(R.drawable.gradient_card_1)
            }

            b.plTracks.layoutManager = LinearLayoutManager(requireContext())
            b.plTracks.adapter = PlaylistTrackAdapter(playlist.tracks) { index ->
                playerViewModel.play(playlist.tracks, index)
                (activity as? MainActivity)?.showNowPlaying()
            }

            b.plPlay.setOnClickListener {
                if (playlist.tracks.isNotEmpty()) {
                    playerViewModel.play(playlist.tracks, 0)
                    (activity as? MainActivity)?.showNowPlaying()
                }
            }
            b.plShuffle.setOnClickListener {
                if (playlist.tracks.isNotEmpty()) {
                    playerViewModel.play(playlist.tracks.shuffled(), 0)
                    (activity as? MainActivity)?.showNowPlaying()
                }
            }
        }
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"
        fun newInstance(playlistId: String) = PlaylistDetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_PLAYLIST_ID, playlistId) }
        }
    }
}
