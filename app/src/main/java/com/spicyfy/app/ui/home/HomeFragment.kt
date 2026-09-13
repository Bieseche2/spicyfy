package com.spicyfy.app.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spicyfy.app.R
import com.spicyfy.app.databinding.FragmentHomeBinding
import com.spicyfy.app.player.PlayerViewModel
import com.spicyfy.app.ui.library.PlaylistAdapter

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by activityViewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = FragmentHomeBinding.bind(view)

        b.homePlaylists.layoutManager = LinearLayoutManager(requireContext())

        val playlistAdapter = PlaylistAdapter { playlist ->
            (activity as? com.spicyfy.app.ui.MainActivity)?.openPlaylist(playlist.id)
        }
        b.homePlaylists.adapter = playlistAdapter

        homeViewModel.recentTracks.observe(viewLifecycleOwner) { tracks ->
            b.homeRecentEmpty.visibility = if (tracks.isEmpty()) View.VISIBLE else View.GONE
            b.homeRecentTracks.visibility = if (tracks.isEmpty()) View.GONE else View.VISIBLE
            if (tracks.isNotEmpty()) {
                b.homeRecentTracks.layoutManager = LinearLayoutManager(
                    requireContext(), RecyclerView.HORIZONTAL, false
                )
                b.homeRecentTracks.adapter = HomeTrackAdapter(tracks) { index ->
                    playerViewModel.play(tracks, index)
                    (activity as? com.spicyfy.app.ui.MainActivity)?.showNowPlaying()
                }
            }
        }

        homeViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            b.homePlaylistsEmpty.visibility = if (playlists.isEmpty()) View.VISIBLE else View.GONE
            playlistAdapter.submitList(playlists)
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.refresh()
    }
}
