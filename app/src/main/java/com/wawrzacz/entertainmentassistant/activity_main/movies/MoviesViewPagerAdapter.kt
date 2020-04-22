package com.wawrzacz.entertainmentassistant.activity_main.movies

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MoviesViewPagerAdapter(val fragment: Fragment): FragmentStateAdapter(fragment) {
    private val itemCount = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> MoviesFavouritesFragment()
            2 -> MoviesWatchedFragment()
            else -> MoviesToWatchFragment()
        }
    }

    override fun getItemCount(): Int {
        return itemCount
    }
}