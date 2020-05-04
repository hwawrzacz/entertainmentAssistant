package com.wawrzacz.entertainmentassistant.activity_main.movies

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.model.SearchResult
import com.wawrzacz.entertainmentassistant.data.enums.Section
import com.wawrzacz.entertainmentassistant.databinding.FragmentMoviesListBinding
import com.wawrzacz.entertainmentassistant.ui.adapters.CommonListAdapter

class MoviesListFragment(private val section: Section): Fragment(),
    MoviesFABFragment {

    private lateinit var binding: FragmentMoviesListBinding
    private lateinit var moviesViewModel: MoviesViewModel
    private lateinit var moviesAdapter: CommonListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_movies_list, container, false)

        setHasOptionsMenu(true)
        initializeViewModel()
        initializeRecyclerView()
        addViewModelObservers()
        findMovies()

        return binding.root
    }

    override fun openAddMovieDialog() {
        Toast.makeText(requireContext(), "Open add movie_24#watched dialog", Toast.LENGTH_LONG).show()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchItem = menu.findItem(R.id.nav_search)
        val searchView = searchItem?.actionView as SearchView?

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                // TODO: Handle query search
                return true
            }
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
        })

        super.onPrepareOptionsMenu(menu)
    }

    private fun initializeRecyclerView() {
        val moviesLayoutManager = LinearLayoutManager(context)
        moviesAdapter = CommonListAdapter(moviesViewModel)

        binding.moviesRecyclerView.apply {
            layoutManager = moviesLayoutManager
            adapter = moviesAdapter
        }
    }

    private fun initializeViewModel() {
        moviesViewModel = ViewModelProvider(requireActivity().viewModelStore, MoviesViewModelFactory())
            .get(MoviesViewModel::class.java)
    }

    private fun addViewModelObservers() {
        moviesViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        })

        moviesViewModel.hasAnyResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                SearchResult.NOT_INITIALIZED -> {
                    binding.bannerMessage.text = getString(R.string.message_action_search_for_movies)
                }
                SearchResult.ERROR_GETTING_DATA -> {
                    binding.bannerMessage.text = getString(R.string.error_getting_data)
                }
                SearchResult.NO_RESULTS -> {
                    binding.bannerMessage.text = getString(R.string.message_no_results)
                    binding.bannerMessage.visibility = View.VISIBLE
                }
                SearchResult.SUCCESS -> {
                    binding.bannerMessage.visibility = View.GONE
                }

            }
        })

        when (section) {
            Section.TO_WATCH -> moviesViewModel.foundToWatchMovies.observe(viewLifecycleOwner, Observer {
                    if (it != null)
                        refreshData(it)
                })
            Section.WATCHED -> moviesViewModel.foundWatchedMovies.observe(viewLifecycleOwner, Observer {
                    if (it != null)
                        refreshData(it)
                })
            Section.FAVOURITES -> moviesViewModel.foundFavouritesMovies.observe(viewLifecycleOwner, Observer {
                    if (it != null)
                        refreshData(it)
                })
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

    private fun findMovies() {
        moviesViewModel.findMovies(section, "")
    }

    private fun refreshData(data: List<CommonListItem>) {
        moviesAdapter.submitList(data)
    }

    private fun openMovieDetailsFragment(id: String) {
        val activity = requireActivity() as MainActivity
        val fragmentManager = requireActivity().supportFragmentManager
        val fragment = MovieDetailsFragment(id)

        activity.hideKeyboard()
        fragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(android.R.id.content, fragment, "DETAILS_FRAGMENT")
            addToBackStack("DETAILS_FRAGMENT")
        }.commit()
    }
}