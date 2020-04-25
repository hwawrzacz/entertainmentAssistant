package com.wawrzacz.entertainmentassistant.activity_main.movies.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wawrzacz.entertainmentassistant.activity_main.movies.MoviesListFragment

class MoviesViewPagerAdapter(
    val fragment: Fragment,
    private val fragments: List<MoviesListFragment>
): FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return fragments[position] as Fragment
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}