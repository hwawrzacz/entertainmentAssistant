package com.wawrzacz.entertainmentassistant.activity_main.browse

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
import com.wawrzacz.entertainmentassistant.activity_main.games.GameDetailsFragment
import com.wawrzacz.entertainmentassistant.activity_main.movies.details.MovieDetailsFragment
import com.wawrzacz.entertainmentassistant.activity_main.series.details.SeriesDetailsFragment
import com.wawrzacz.entertainmentassistant.data.enums.ItemType
import com.wawrzacz.entertainmentassistant.data.enums.MediaCategory
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.databinding.FragmentBrowseBinding
import com.wawrzacz.entertainmentassistant.ui.adapters.CommonListAdapter

class BrowseFragment: Fragment() {

    private lateinit var binding: FragmentBrowseBinding
    private lateinit var browseViewModel: BrowseViewModel
    private lateinit var moviesAdapter: CommonListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_browse, container, false)

        setHasOptionsMenu(true)

        initializeViewModel()
        initializeRecyclerView()
        addViewModelObservers()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = (requireActivity() as MainActivity)
        activity.changeCurrentCategory(MediaCategory.BROWSE)
        val actionBar = activity.supportActionBar
        actionBar?.title = getString(R.string.label_browse)
        actionBar?.setIcon(R.drawable.browse_24)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchItem = menu.findItem(R.id.nav_search)
        val searchView = searchItem?.actionView as SearchView?

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                browseViewModel.setQuery(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean { return false }
        })

        super.onPrepareOptionsMenu(menu)
    }

    private fun initializeRecyclerView() {
        moviesAdapter = CommonListAdapter(browseViewModel)
        val moviesLayoutManager = LinearLayoutManager(context)

        binding.moviesRecyclerView.apply {
            layoutManager = moviesLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = moviesAdapter
        }
    }

    private fun initializeViewModel() {
        browseViewModel = ViewModelProvider(viewModelStore, BrowseViewModelFactory())
            .get(BrowseViewModel::class.java)
    }

    private fun addViewModelObservers() {
        browseViewModel.foundItems.observe(viewLifecycleOwner, Observer {
            refreshData(it)
        })

        browseViewModel.responseStatus.observe(viewLifecycleOwner, Observer {
            if (it != null)
                when (it) {
                    ResponseStatus.NOT_INITIALIZED -> {
                        hideProgressBar()
                        hideResults()
                        showBanner(getString(R.string.message_action_browse))
                    }
                    ResponseStatus.IN_PROGRESS -> {
                        Log.i("schab", "IN_PROGRESS")
                        hideResults()
                        hideBanner()
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
        })

        browseViewModel.selectedItem.observe(viewLifecycleOwner, Observer {
            if (it !== null) {
                val activity = requireActivity() as MainActivity
                activity.clearFocusFromSearchView()
                activity.initializeActionBar()

                when (it.type){
                    ItemType.MOVIE -> openMovieDetailsFragment(it.id)
                    ItemType.SERIES -> openSeriesDetailsFragment(it.id)
                    ItemType.GAME -> openGameDetailsFragment(it.id)
                }
            }
        })
    }

    private fun refreshData(data: List<CommonListItem>?) {
        moviesAdapter.submitList(data)
        moviesAdapter.notifyDataSetChanged()
    }

    private fun openMovieDetailsFragment(id: String) {
        val movieDetailsFragment = MovieDetailsFragment(id, binding.moviesRecyclerView)
        openDetailsFragment(movieDetailsFragment)
    }

    private fun openSeriesDetailsFragment(id: String) {
        val seriesDetailsFragment = SeriesDetailsFragment(id, binding.moviesRecyclerView)
        openDetailsFragment(seriesDetailsFragment)
    }

    private fun openGameDetailsFragment(id: String) {
        val gamesDetailsFragment = GameDetailsFragment(id)
        openDetailsFragment(gamesDetailsFragment)
    }

    private fun openDetailsFragment(fragment: Fragment) {
        val activity = requireActivity() as MainActivity
        val fragmentManager = requireActivity().supportFragmentManager

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
        binding.bannerMessage.text = string
        binding.bannerMessage.visibility = View.VISIBLE
    }

    private fun hideBanner() {
        binding.bannerMessage.visibility = View.GONE
    }

    private fun showResults() {
        binding.moviesRecyclerView.visibility = View.VISIBLE
    }

    private fun hideResults() {
        binding.moviesRecyclerView.visibility = View.GONE
    }
    //#endregion
}