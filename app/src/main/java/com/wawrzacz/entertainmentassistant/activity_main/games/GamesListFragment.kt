package com.wawrzacz.entertainmentassistant.activity_main.games

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
import com.wawrzacz.entertainmentassistant.activity_main.games.details.GameDetailsFragment
import com.wawrzacz.entertainmentassistant.data.enums.PlayableSection
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.databinding.FragmentListBinding
import com.wawrzacz.entertainmentassistant.ui.adapters.CommonListAdapter

abstract class GamesListFragment: Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var gamesViewModel: GamesViewModel
    private lateinit var gamesAdapter: CommonListAdapter
    protected abstract val section: PlayableSection

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_list, container, false)

        setHasOptionsMenu(true)
        initializeViewModel()
        initializeRecyclerView()
        addViewModelObservers()
        findGames(null)

        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchItem = menu.findItem(R.id.nav_search)
        val searchView = searchItem?.actionView as SearchView?

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                findGames(newText)
                return true
            }
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
        })

        super.onPrepareOptionsMenu(menu)
    }

    private fun initializeRecyclerView() {
        val gamesLayoutManager = LinearLayoutManager(context)
        gamesAdapter = CommonListAdapter(gamesViewModel)

        binding.recyclerView.apply {
            layoutManager = gamesLayoutManager
            adapter = gamesAdapter
        }
    }

    private fun initializeViewModel() {
        gamesViewModel = ViewModelProvider(requireActivity().viewModelStore, GamesViewModelFactory())
            .get(GamesViewModel::class.java)
    }

    private fun addViewModelObservers() {
        when (section) {
            PlayableSection.TO_PLAY -> {
                gamesViewModel.foundToWatchGames.observe(viewLifecycleOwner, Observer {
                    refreshData(it)
                })
                gamesViewModel.responseToWatchStatus.observe(viewLifecycleOwner, Observer {
                    handleResponseStatusChange(it)
                })
            }
            PlayableSection.PLAYED -> {
                gamesViewModel.foundWatchedGames.observe(viewLifecycleOwner, Observer {
                    refreshData(it)
                })
                gamesViewModel.responseWatchedStatus.observe(viewLifecycleOwner, Observer {
                    handleResponseStatusChange(it)
                })
            }
            PlayableSection.FAVOURITES -> {
                gamesViewModel.foundFavouritesGames.observe(viewLifecycleOwner, Observer {
                    refreshData(it)
                })
                gamesViewModel.responseFavouritesStatus.observe(viewLifecycleOwner, Observer {
                    handleResponseStatusChange(it)
                })
            }
        }

        gamesViewModel.selectedItem.observe(viewLifecycleOwner, Observer {
            if (it !== null) {
                val activity = requireActivity() as MainActivity
                activity.clearFocusFromSearchView()
                activity.initializeActionBar()

                openGameDetailsFragment(it.id)
            }
        })
    }

    private fun handleResponseStatusChange(responseStatus: ResponseStatus) {
        when (responseStatus) {
            ResponseStatus.NOT_INITIALIZED -> {
                hideProgressBar()
                hideResults()
                showBanner(getString(R.string.message_action_browse))
            }
            ResponseStatus.IN_PROGRESS -> {
                hideBanner()
                hideResults()
                showProgressBar()
            }
            ResponseStatus.SUCCESS -> {
                hideBanner()
                hideProgressBar()
                showResults()
            }
            ResponseStatus.NO_RESULT -> {
                hideProgressBar()
                hideResults()
                showBanner(getString(R.string.message_no_results))
            }
            ResponseStatus.ERROR -> {
                hideProgressBar()
                hideResults()
                showBanner(getString(R.string.error_getting_data))
            }
        }
    }

    private fun findGames(query: String?) {
        gamesViewModel.findGames(section, query)
    }

    private fun refreshData(data: List<CommonListItem>?) {
        gamesAdapter.submitList(data)
        gamesAdapter.notifyDataSetChanged()
    }

    private fun openGameDetailsFragment(id: String) {
        val activity = requireActivity() as MainActivity
        val fragmentManager = requireActivity().supportFragmentManager
        val fragment =
            GameDetailsFragment(id, binding.recyclerView)

        activity.hideKeyboard()
        fragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(android.R.id.content, fragment, "DETAILS_FRAGMENT")
        }.commit()
    }

    //#region UI changes
    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showBanner(string: String) {
        binding.bannerMessage.visibility = View.VISIBLE
        binding.bannerMessage.text = string
    }

    private fun hideBanner() {
        binding.bannerMessage.visibility = View.GONE
    }

    private fun showResults() {
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun hideResults() {
        binding.recyclerView.visibility = View.GONE
    }
    //#endregion
}