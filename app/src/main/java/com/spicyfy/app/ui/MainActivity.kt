package com.spicyfy.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spicyfy.app.R
import com.spicyfy.app.ui.home.HomeFragment
import com.spicyfy.app.ui.library.LibraryFragment
import com.spicyfy.app.ui.nowplaying.NowPlayingFragment
import com.spicyfy.app.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val nowPlayingFragment = NowPlayingFragment()
    private val libraryFragment = LibraryFragment()

    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, libraryFragment, "library").hide(libraryFragment)
                .add(R.id.fragment_container, nowPlayingFragment, "now_playing").hide(nowPlayingFragment)
                .add(R.id.fragment_container, searchFragment, "search").hide(searchFragment)
                .add(R.id.fragment_container, homeFragment, "home")
                .commit()
        }

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener { item ->
            val destino = when (item.itemId) {
                R.id.nav_home -> homeFragment
                R.id.nav_search -> searchFragment
                R.id.nav_now_playing -> nowPlayingFragment
                R.id.nav_library -> libraryFragment
                else -> return@setOnItemSelectedListener false
            }
            switchTo(destino)
            true
        }
    }

    fun showNowPlaying() {
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.nav_now_playing
    }

    fun openPlaylist(playlistId: String) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out
            )
            .add(R.id.fragment_container, com.spicyfy.app.ui.library.PlaylistDetailFragment.newInstance(playlistId))
            .addToBackStack("playlist_detail")
            .commit()
    }

    private fun switchTo(destino: Fragment) {
        if (destino === activeFragment) return
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .hide(activeFragment)
            .show(destino)
            .commit()
        activeFragment = destino
    }
}
