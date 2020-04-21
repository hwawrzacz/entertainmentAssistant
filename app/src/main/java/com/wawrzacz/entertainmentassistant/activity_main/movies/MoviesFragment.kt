package com.wawrzacz.entertainmentassistant.activity_main.movies

import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.databinding.FragmentMoviesBinding
import java.io.InputStream
import java.io.InputStreamReader

class MoviesFragment: Fragment() {
    private lateinit var binding: FragmentMoviesBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_movies, container, false)

        initializeViewPager()
        initializeTabLayout()

        return binding.root
    }

    private fun initializeTabLayout() {
        tabLayout = binding.tabLayout

        val tabLayoutMediator = TabLayoutMediator(
            tabLayout,
            viewPager,
            TabLayoutMediator
                .TabConfigurationStrategy {
                    tab, position -> run {
                        when (position) {
                            0 -> {
                                tab.text = getString(R.string.label_to_watch)
                                tab.icon = resources.getDrawable(R.drawable.bookmark_24, requireActivity().theme)
                            }
                            1 -> {
                                tab.text = getString(R.string.label_favourites)
                                tab.icon = resources.getDrawable(R.drawable.heart_filled_24, requireActivity().theme)
                            }
                            2 ->{
                                tab.text = getString(R.string.label_watched)
                                tab.icon = resources.getDrawable(R.drawable.eye_24, requireActivity().theme)
                            }
                        }
                    }
            }
            ).attach()
    }

    private fun initializeViewPager() {
        viewPager = binding.moviesViewPager
        viewPager.adapter = MoviesViewPagerAdapter(this)
    }
}