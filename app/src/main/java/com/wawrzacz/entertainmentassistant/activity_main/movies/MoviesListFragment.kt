package com.wawrzacz.entertainmentassistant.activity_main.movies

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.browse.BrowseViewModel
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem
import com.wawrzacz.entertainmentassistant.databinding.FragmentMoviesWatchedBinding
import com.wawrzacz.entertainmentassistant.ui.adapters.BrowseListAdapter

class MoviesListFragment(private val section: String): Fragment(),
    MoviesFABFragment {

    private lateinit var binding: FragmentMoviesWatchedBinding
    private lateinit var moviesViewModel: MoviesViewModel
    private val moviesAdapter = BrowseListAdapter(BrowseViewModel())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_movies_watched, container, false)
        setHasOptionsMenu(true)

        initializeRecyclerView()
        initializeViewModel()
        addViewModelObservers()
        loadData()

        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchItem = menu.findItem(R.id.nav_search)
        val searchView = searchItem?.actionView as SearchView?

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                binding.search.text = "Search watched: $newText"

                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean { return false }
        })

        super.onPrepareOptionsMenu(menu)
    }

    override fun openAddMovieDialog() {
        Toast.makeText(requireContext(), "Open add movie_24#watched dialog", Toast.LENGTH_LONG).show()
    }

    private fun initializeRecyclerView() {
        val moviesLayoutManager = LinearLayoutManager(context)

        binding.moviesRecyclerView.apply {
            layoutManager = moviesLayoutManager
            adapter = moviesAdapter
        }
    }

    private fun initializeViewModel() {
        moviesViewModel = ViewModelProvider(this, MoviesViewModelFactory())
            .get(MoviesViewModel::class.java)
    }

    private fun addViewModelObservers() {
        moviesViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        })

        when (section) {
            "to_watch" -> moviesViewModel.foundToWatchMovies.observe(viewLifecycleOwner, Observer {
                    if (it != null)
                        refreshData(it)
                })
            "watched" -> moviesViewModel.foundWatchedMovies.observe(viewLifecycleOwner, Observer {
                if (it != null)
                    refreshData(it)
                })
            "favourites" -> moviesViewModel.foundFavouritesMovies.observe(viewLifecycleOwner, Observer {
                    if (it != null)
                        refreshData(it)
                })
        }

    }

    private fun findMovies(query: String) {
        moviesViewModel.findMovies(section, query)
    }

    private fun loadData() {
        moviesViewModel.findMovies(section, "")
    }

    private fun refreshData(data: List<UniversalItem>) {
        moviesAdapter.submitList(data)
    }
}