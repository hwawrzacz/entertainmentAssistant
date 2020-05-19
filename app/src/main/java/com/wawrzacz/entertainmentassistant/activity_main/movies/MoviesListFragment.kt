package com.wawrzacz.entertainmentassistant.activity_main.movies

import android.os.Bundle
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
import com.wawrzacz.entertainmentassistant.activity_main.movies.details.MovieDetailsFragment
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.databinding.FragmentListBinding
import com.wawrzacz.entertainmentassistant.ui.adapters.CommonListAdapter

abstract class MoviesListFragment: Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var moviesViewModel: MoviesViewModel
    private lateinit var moviesAdapter: CommonListAdapter
    protected abstract val section: WatchableSection

//    companion object {
//        fun newInstance(section: WatchableSection): MoviesListFragment {
//            val fragment = MoviesListFragment()
//            fragment.section = section
//            return fragment
//        }
//    }

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
        findMovies(null)

        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchItem = menu.findItem(R.id.nav_search)
        val searchView = searchItem?.actionView as SearchView?

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                findMovies(newText)
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
        moviesViewModel = ViewModelProvider(requireActivity().viewModelStore, MoviesViewModelFactory())
            .get(MoviesViewModel::class.java)
    }

    private fun addViewModelObservers() {
        when (section) {
            WatchableSection.TO_WATCH -> {
                moviesViewModel.foundToWatchMovies.observe(viewLifecycleOwner, Observer {
                    refreshData(it)
                })
                moviesViewModel.responseToWatchStatus.observe(viewLifecycleOwner, Observer {
                    handleResponseStatusChange(it)
                })
            }
            WatchableSection.WATCHED -> {
                moviesViewModel.foundWatchedMovies.observe(viewLifecycleOwner, Observer {
                    refreshData(it)
                })
                moviesViewModel.responseWatchedStatus.observe(viewLifecycleOwner, Observer {
                    handleResponseStatusChange(it)
                })
            }
            WatchableSection.FAVOURITES -> {
                moviesViewModel.foundFavouritesMovies.observe(viewLifecycleOwner, Observer {
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

                openMovieDetailsFragment(it.id)
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

    private fun findMovies(query: String?) {
        moviesViewModel.findMovies(section, query)
    }

    private fun refreshData(data: List<CommonListItem>?) {
        moviesAdapter.submitList(data)
        moviesAdapter.notifyDataSetChanged()
    }

    private fun openMovieDetailsFragment(id: String) {
        val activity = requireActivity() as MainActivity
        val fragmentManager = requireActivity().supportFragmentManager
        val fragment =
            MovieDetailsFragment(id, binding.recyclerView)

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