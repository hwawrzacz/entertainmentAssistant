package com.wawrzacz.entertainmentassistant.activity_main.movies.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wawrzacz.entertainmentassistant.activity_main.movies.MoviesAllFragment
import com.wawrzacz.entertainmentassistant.activity_main.movies.MoviesListFragment
import com.wawrzacz.entertainmentassistant.activity_main.movies.MoviesToWatchFragment
import com.wawrzacz.entertainmentassistant.activity_main.movies.MoviesWatchedFragment

class MoviesViewPagerAdapter(val fragment: Fragment, val fragments: List<MoviesListFragment>): FragmentStateAdapter(fragment) {
    private val itemCount = 3

    override fun createFragment(position: Int): Fragment {
        return fragments[position] as Fragment
    }

    override fun getItemCount(): Int {
        return itemCount
    }
}