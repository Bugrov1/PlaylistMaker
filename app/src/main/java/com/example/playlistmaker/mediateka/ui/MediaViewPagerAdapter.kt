package com.example.playlistmaker.mediateka.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.mediateka.ui.fragments.MediaFragmentFavourites
import com.example.playlistmaker.mediateka.ui.fragments.MediaFragmentPlaylists

class MediaViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> MediaFragmentFavourites.newInstance("Ваша медиатека пуста")
            1 -> MediaFragmentPlaylists.newInstance("Вы не создали ни одного плейлиста")
            else -> MediaFragmentFavourites.newInstance("Ваша медиатека пуста")
        }
    }
}