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
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpActionBar()
    }

    private fun initializeTabLayout() {
        tabLayout = binding.tabLayout

        TabLayoutMediator(
            tabLayout,
            viewPager,
            TabLayoutMediator
                .TabConfigurationStrategy {
                    tab, position -> run {
                        when (position) {
                            0 -> {
                                tab.text = getString(R.string.label_to_watch)
                            }
                            1 -> {
                                tab.text = getString(R.string.label_favourites)
                            }
                            2 ->{
                                tab.text = getString(R.string.label_watched)
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

    private fun setUpActionBar() {
        (requireActivity() as MainActivity).setActionBarTitle(getString(R.string.label_movies))
        (requireActivity() as MainActivity).setActionBarIcon(R.drawable.movies_rounded)
    }
}