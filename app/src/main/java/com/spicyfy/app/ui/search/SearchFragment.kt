package com.spicyfy.app.ui.search

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.spicyfy.app.R
import com.spicyfy.app.databinding.FragmentSearchBinding
import com.spicyfy.app.extractor.YoutubeMusicSource
import com.spicyfy.app.player.PlayerViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val playerViewModel: PlayerViewModel by activityViewModels()
    private val musicSource = YoutubeMusicSource()
    private var searchJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = FragmentSearchBinding.bind(view)

        val adapter = TrackAdapter { track -> playerViewModel.play(track) }
        b.searchResults.layoutManager = LinearLayoutManager(requireContext())
        b.searchResults.adapter = adapter

        b.searchInput.addTextChangedListener { text ->
            val query = text?.toString()?.trim().orEmpty()
            searchJob?.cancel()

            if (query.length < 2) {
                b.searchIdleState.visibility = View.VISIBLE
                b.searchResults.visibility = View.GONE
                adapter.submitList(emptyList())
                return@addTextChangedListener
            }

            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(400)
                val results = runCatching { musicSource.search(query) }.getOrDefault(emptyList())
                b.searchIdleState.visibility = View.GONE
                b.searchResults.visibility = View.VISIBLE
                adapter.submitList(results)
            }
        }
    }
}
