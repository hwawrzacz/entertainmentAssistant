package com.wawrzacz.entertainmentassistant.activity_main.games

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
import com.wawrzacz.entertainmentassistant.data.enums.PlayableSection
import com.wawrzacz.entertainmentassistant.databinding.FragmentTabsBinding
import com.wawrzacz.entertainmentassistant.ui.adapters.MyViewPagerAdapter

class GamesTabsFragment: Fragment() {
    private lateinit var binding: FragmentTabsBinding

    private lateinit var viewPager: ViewPager2
    private val gamesFragments = listOf(
        GamesListFragment(
            PlayableSection.TO_PLAY
        ),
        GamesListFragment(
            PlayableSection.PLAYED
        ),
        GamesListFragment(
            PlayableSection.FAVOURITES
        )
    )
    private var currentMovieFragment: GamesListFragment = gamesFragments[0]

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_tabs, container, false)

        initializeViewPager()
        initializeTabLayout()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = (requireActivity() as MainActivity)
        activity.setCurrentCategory(MediaCategory.GAMES)
        setUpActionBar()
        setTabChangeListeners()
    }

    private fun initializeViewPager() {
        viewPager = binding.viewPager
        viewPager.adapter = MyViewPagerAdapter(this, gamesFragments)
    }

    private fun initializeTabLayout() {
        val tabLayout = binding.tabLayout

        TabLayoutMediator( tabLayout, viewPager, TabLayoutMediator
            .TabConfigurationStrategy {
                tab, position -> run {
                    when (position) {
                        0 -> tab.text = getString(R.string.label_to_play)
                        1 -> tab.text = getString(R.string.label_played)
                        2 -> tab.text = getString(R.string.label_favourites)
                    }
                }
            }
        ).attach()
    }

    private fun setUpActionBar() {
        (requireActivity() as MainActivity).setActionBarTitle(getString(R.string.label_games))
        (requireActivity() as MainActivity).setActionBarIcon(R.drawable.gamepad_filled)
    }

    private fun setTabChangeListeners() {
        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val currentPosition = tab?.position
                hideKeyboard()
                if (currentPosition != null)
                    currentMovieFragment = gamesFragments[currentPosition]
            }
        })
    }

    private fun hideKeyboard() {
        (requireActivity() as MainActivity).hideKeyboard()
    }
}