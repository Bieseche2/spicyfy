package com.spicyfy.app.ui.nowplaying

import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.children
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.spicyfy.app.R
import com.spicyfy.app.databinding.FragmentNowPlayingBinding
import com.spicyfy.app.lyrics.LyricLine
import com.spicyfy.app.player.PlayerViewModel
import com.spicyfy.app.ui.library.LibraryViewModel

class NowPlayingFragment : Fragment(R.layout.fragment_now_playing) {

    private val playerViewModel: PlayerViewModel by activityViewModels()
    private val libraryViewModel: LibraryViewModel by activityViewModels()
    private var binding: FragmentNowPlayingBinding? = null

    private var isUserSeeking = false
    private var currentLyrics: List<LyricLine> = emptyList()
    private var highlightedLineIndex = -1

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
        b.npNext.setOnClickListener { playerViewModel.nextTrack() }
        b.npPrevious.setOnClickListener { playerViewModel.previousTrack() }
        b.npAddPlaylist.setOnClickListener { showAddToPlaylistDialog() }

        b.npProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) { isUserSeeking = true }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = false
                playerViewModel.seekToPercent(seekBar?.progress ?: 0)
            }
        })

        playerViewModel.isPlaying.observe(viewLifecycleOwner) { playing ->
            b.npPlayPause.setImageResource(if (playing) R.drawable.ic_pause else R.drawable.ic_play)
        }

        playerViewModel.currentTrack.observe(viewLifecycleOwner) { track ->
            if (track != null) {
                b.npTitle.text = track.title
                b.npArtist.text = track.artist
                b.npCover.load(track.coverUrl) {
                    crossfade(true)
                    placeholder(R.drawable.gradient_card_1)
                    error(R.drawable.gradient_card_1)
                }
            }
        }

        playerViewModel.progressPercent.observe(viewLifecycleOwner) { percent ->
            if (!isUserSeeking) b.npProgress.progress = percent
        }
        playerViewModel.positionText.observe(viewLifecycleOwner) { b.npTimeCurrent.text = it }
        playerViewModel.durationText.observe(viewLifecycleOwner) { b.npTimeTotal.text = it }

        playerViewModel.lyrics.observe(viewLifecycleOwner) { lines ->
            currentLyrics = lines.orEmpty()
            highlightedLineIndex = -1
            renderLyrics(b)
        }

        playerViewModel.positionMs.observe(viewLifecycleOwner) { position ->
            updateLyricsHighlight(b, position)
        }
    }

    private fun renderLyrics(b: FragmentNowPlayingBinding) {
        b.npLyricsContainer.removeAllViews()
        if (currentLyrics.isEmpty()) return
        currentLyrics.forEach { line ->
            val textView = TextView(requireContext()).apply {
                text = line.text
                setTextColor(resources.getColor(R.color.text_low, null))
                textSize = 19f
                setPadding(0, 12, 0, 12)
            }
            b.npLyricsContainer.addView(textView)
        }
    }

    private fun updateLyricsHighlight(b: FragmentNowPlayingBinding, positionMs: Long) {
        if (currentLyrics.isEmpty()) return
        val activeIndex = currentLyrics.indexOfLast { it.timeMs <= positionMs }
        if (activeIndex == highlightedLineIndex) return
        highlightedLineIndex = activeIndex

        b.npLyricsContainer.children.forEachIndexed { index, view ->
            val textView = view as? TextView ?: return@forEachIndexed
            val isActive = index == activeIndex
            textView.setTextColor(
                resources.getColor(if (isActive) R.color.text_hi else R.color.text_low, null)
            )
            textView.textSize = if (isActive) 21f else 19f
        }

        if (activeIndex >= 0 && activeIndex < b.npLyricsContainer.childCount) {
            val destino = b.npLyricsContainer.getChildAt(activeIndex)
            b.npLyricsScroll.smoothScrollTo(0, destino.top - 100)
        }
    }

    private fun showAddToPlaylistDialog() {
        val track = playerViewModel.currentTrack.value ?: return
        val playlists = libraryViewModel.playlists.value.orEmpty()

        if (playlists.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.new_playlist)
                .setPositiveButton(R.string.create) { _, _ ->
                    libraryViewModel.createPlaylist(track.artist.ifBlank { track.title })
                    libraryViewModel.playlists.value?.lastOrNull()?.let {
                        libraryViewModel.addTrackToPlaylist(it.id, track)
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
            return
        }

        val names = playlists.map { it.name }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_to_playlist)
            .setItems(names) { _, which ->
                libraryViewModel.addTrackToPlaylist(playlists[which].id, track)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
