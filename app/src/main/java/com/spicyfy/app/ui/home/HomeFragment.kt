package com.spicyfy.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spicyfy.app.R
import com.spicyfy.app.data.model.Playlist
import com.spicyfy.app.data.model.Track
import com.spicyfy.app.databinding.FragmentHomeBinding
import com.spicyfy.app.databinding.ItemHomeRecommendationRowBinding
import com.spicyfy.app.player.PlayerViewModel
import com.spicyfy.app.ui.library.PlaylistAdapter

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by activityViewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()

    private var lastPlaylists: List<Playlist> = emptyList()
    private var lastRecent: List<Track> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = FragmentHomeBinding.bind(view)

        b.homeQuickAccess.layoutManager = GridLayoutManager(requireContext(), 2)

        b.homePlaylists.layoutManager = LinearLayoutManager(requireContext())
        val playlistAdapter = PlaylistAdapter { playlist ->
            (activity as? com.spicyfy.app.ui.MainActivity)?.openPlaylist(playlist.id)
        }
        b.homePlaylists.adapter = playlistAdapter

        b.homeRecentTracks.layoutManager = LinearLayoutManager(
            requireContext(), RecyclerView.HORIZONTAL, false
        )

        homeViewModel.recommendationRows.observe(viewLifecycleOwner) { rows ->
            b.homeRecommendedContainer.removeAllViews()
            rows.forEach { row ->
                val rowBinding = ItemHomeRecommendationRowBinding.inflate(
                    LayoutInflater.from(requireContext()), b.homeRecommendedContainer, false
                )
                rowBinding.rowTitle.text = row.title
                rowBinding.rowTracks.layoutManager = LinearLayoutManager(
                    requireContext(), RecyclerView.HORIZONTAL, false
                )
                rowBinding.rowTracks.adapter = HomeTrackAdapter(row.tracks) { index ->
                    playerViewModel.play(row.tracks, index)
                    (activity as? com.spicyfy.app.ui.MainActivity)?.showNowPlaying()
                }
                b.homeRecommendedContainer.addView(rowBinding.root)
            }
        }

        homeViewModel.recentTracks.observe(viewLifecycleOwner) { tracks ->
            lastRecent = tracks
            b.homeRecentEmpty.visibility = if (tracks.isEmpty()) View.VISIBLE else View.GONE
            b.homeRecentTracks.visibility = if (tracks.isEmpty()) View.GONE else View.VISIBLE
            if (tracks.isNotEmpty()) {
                b.homeRecentTracks.adapter = HomeTrackAdapter(tracks) { index ->
                    playerViewModel.play(tracks, index)
                    (activity as? com.spicyfy.app.ui.MainActivity)?.showNowPlaying()
                }
            }
            updateQuickAccess(b)
        }

        homeViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            lastPlaylists = playlists
            b.homePlaylistsEmpty.visibility = if (playlists.isEmpty()) View.VISIBLE else View.GONE
            playlistAdapter.submitList(playlists)
            updateQuickAccess(b)
        }
    }

    private fun updateQuickAccess(b: FragmentHomeBinding) {
        val items = mutableListOf<QuickAccessItem>()

        lastPlaylists.take(2).forEach { playlist ->
            items.add(
                QuickAccessItem(
                    title = playlist.name,
                    coverUrl = playlist.tracks.firstOrNull()?.coverUrl,
                    onClick = { (activity as? com.spicyfy.app.ui.MainActivity)?.openPlaylist(playlist.id) }
                )
            )
        }

        lastRecent.take(4 - items.size).forEach { track ->
            items.add(
                QuickAccessItem(
                    title = track.title,
                    coverUrl = track.coverUrl,
                    onClick = {
                        playerViewModel.play(lastRecent, lastRecent.indexOf(track))
                        (activity as? com.spicyfy.app.ui.MainActivity)?.showNowPlaying()
                    }
                )
            )
        }

        b.homeQuickAccess.adapter = QuickAccessAdapter(items)
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.refresh()
    }
}
