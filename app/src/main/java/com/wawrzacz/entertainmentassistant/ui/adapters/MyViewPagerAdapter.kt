package com.wawrzacz.entertainmentassistant.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyViewPagerAdapter(
    val fragment: Fragment,
    private val fragments: List<Fragment>
): FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return fragments[position] as Fragment
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}