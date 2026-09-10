package com.spicyfy.app.ui.nowplaying

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.spicyfy.app.R
import com.spicyfy.app.databinding.FragmentNowPlayingBinding
import com.spicyfy.app.player.PlayerViewModel

class NowPlayingFragment : Fragment(R.layout.fragment_now_playing) {

    private val playerViewModel: PlayerViewModel by activityViewModels()
    private var binding: FragmentNowPlayingBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = FragmentNowPlayingBinding.bind(view)
        binding = b

        b.npLyricsToggle.setOnClickListener {
            val showingLyrics = b.npLyricsScroll.visibility == View.VISIBLE
            b.npLyricsScroll.visibility = if (showingLyrics) View.GONE else View.VISIBLE
            b.npControlsGroup.visibility = if (showingLyrics) View.VISIBLE else View.GONE
            b.npLyricsToggle.setText(
                if (showingLyrics) R.string.lyrics_show else R.string.lyrics_hide
            )
        }

        b.npPlayPause.setOnClickListener { playerViewModel.togglePlayPause() }

        playerViewModel.isPlaying.observe(viewLifecycleOwner) { playing ->
            b.npPlayPause.setImageResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play)
        }

        playerViewModel.currentTrack.observe(viewLifecycleOwner) { track ->
            if (track != null) {
                b.npTitle.text = track.title
                b.npArtist.text = track.artist
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
