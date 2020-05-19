package com.wawrzacz.entertainmentassistant.activity_main.series

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
import com.wawrzacz.entertainmentassistant.activity_main.series.details.SeriesDetailsFragment
//import com.wawrzacz.entertainmentassistant.activity_main.movies.SeriesFABFragment
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.databinding.FragmentListBinding
import com.wawrzacz.entertainmentassistant.ui.adapters.CommonListAdapter

abstract class SeriesListFragment: Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var moviesViewModel: SeriesViewModel
    private lateinit var moviesAdapter: CommonListAdapter
    protected abstract val section: WatchableSection

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
        findSeries(null)

        return binding.root
    }

//    override fun openAddSerieDialog() {
//        Toast.makeText(requireContext(), "Open add movie_24#watched dialog", Toast.LENGTH_LONG).show()
//    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchItem = menu.findItem(R.id.nav_search)
        val searchView = searchItem?.actionView as SearchView?

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                findSeries(newText)
                return true
            }
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
        })

        super.onPrepareOptionsMenu(menu)
    }

    private fun initializeRecyclerView() {
        val moviesLayoutManager = LinearLayoutManager(context)
        moviesAdapter = CommonListAdapter(moviesViewModel)

        binding.recyclerView.apply {
            layoutManager = moviesLayoutManager
            adapter = moviesAdapter
        }
    }

    private fun initializeViewModel() {
        moviesViewModel = ViewModelProvider(requireActivity().viewModelStore, SeriesViewModelFactory())
            .get(SeriesViewModel::class.java)
    }

    private fun addViewModelObservers() {
        when (section) {
            WatchableSection.TO_WATCH -> {
                moviesViewModel.foundToWatchSeries.observe(viewLifecycleOwner, Observer {
                    refreshData(it)
                })
                moviesViewModel.responseToWatchStatus.observe(viewLifecycleOwner, Observer {
                    handleResponseStatusChange(it)
                })
            }
            WatchableSection.WATCHED -> {
                moviesViewModel.foundWatchedSeries.observe(viewLifecycleOwner, Observer {
                    refreshData(it)
                })
                moviesViewModel.responseWatchedStatus.observe(viewLifecycleOwner, Observer {
                    handleResponseStatusChange(it)
                })
            }
            WatchableSection.FAVOURITES -> {
                moviesViewModel.foundFavouritesSeries.observe(viewLifecycleOwner, Observer {
                    refreshData(it)
                })
                moviesViewModel.responseFavouritesStatus.observe(viewLifecycleOwner, Observer {
                    handleResponseStatusChange(it)
                })
            }
        }

        moviesViewModel.selectedItem.observe(viewLifecycleOwner, Observer {
            if (it !== null) {
                val activity = requireActivity() as MainActivity
                activity.clearFocusFromSearchView()
                activity.initializeActionBar()

                openSeriesDetailsFragment(it.id)
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

    private fun findSeries(query: String?) {
        moviesViewModel.findSeries(section, query)
    }

    private fun refreshData(data: List<CommonListItem>?) {
        moviesAdapter.submitList(data)
        moviesAdapter.notifyDataSetChanged()
    }

    private fun openSeriesDetailsFragment(id: String) {
        val activity = requireActivity() as MainActivity
        val fragmentManager = requireActivity().supportFragmentManager
        val fragment =
            SeriesDetailsFragment(id, binding.recyclerView)

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