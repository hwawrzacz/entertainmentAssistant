package com.wawrzacz.entertainmentassistant.activity_main.series

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
import com.wawrzacz.entertainmentassistant.data.enums.MediaCategory
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection
import com.wawrzacz.entertainmentassistant.databinding.FragmentTabsWatchableBinding
import com.wawrzacz.entertainmentassistant.ui.adapters.MyViewPagerAdapter

class SeriesTabsFragment: Fragment() {
    private lateinit var binding: FragmentTabsWatchableBinding

    private lateinit var viewPager: ViewPager2
    private val moviesFragments = listOf(
        SeriesListFragment(
            WatchableSection.TO_WATCH
        ),
        SeriesListFragment(
            WatchableSection.WATCHED
        ),
        SeriesListFragment(
            WatchableSection.FAVOURITES
        )
    )
    private var currentSeriesFragment: SeriesListFragment = moviesFragments[0]

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_tabs_watchable, container, false)

        initializeViewPager()
        initializeTabLayout()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = (requireActivity() as MainActivity)
        activity.changeCurrentCategory(MediaCategory.MOVIES)
        setUpActionBar()
        setTabChangeListeners()
    }

    private fun initializeViewPager() {
        viewPager = binding.moviesViewPager
        viewPager.adapter = MyViewPagerAdapter(this, moviesFragments)
    }

    private fun initializeTabLayout() {
        val tabLayout = binding.tabLayout

        TabLayoutMediator( tabLayout, viewPager, TabLayoutMediator
            .TabConfigurationStrategy {
                tab, position -> run {
                    when (position) {
                        0 -> tab.text = getString(R.string.label_to_watch)
                        1 -> tab.text = getString(R.string.label_watched)
                        2 -> tab.text = getString(R.string.label_favourites)
                    }
                }
            }
        ).attach()
    }

    private fun setUpActionBar() {
        (requireActivity() as MainActivity).setActionBarTitle(getString(R.string.label_series))
        (requireActivity() as MainActivity).setActionBarIcon(R.drawable.series_24)
    }

    private fun setTabChangeListeners() {
        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val currentPosition = tab?.position
                hideKeyboard()
                if (currentPosition != null)
                    currentSeriesFragment = moviesFragments[currentPosition]
            }
        })
    }

    private fun hideKeyboard() {
        (requireActivity() as MainActivity).hideKeyboard()
    }
}