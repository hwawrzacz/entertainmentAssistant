package com.wawrzacz.entertainmentassistant.activity_main.movies.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.movies.MoviesListFragment
import com.wawrzacz.entertainmentassistant.activity_main.movies.adapters.MoviesRecyclerViewAdapter
import com.wawrzacz.entertainmentassistant.data.model.MovieSimple
import com.wawrzacz.entertainmentassistant.databinding.FragmentMoviesBrowseBinding

class MoviesBrowseFragment: Fragment(),
    MoviesListFragment {

    private lateinit var binding: FragmentMoviesBrowseBinding
    private lateinit var moviesViewModel: MoviesBrowseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_movies_browse, container, false)

        setHasOptionsMenu(true)

        initializeRecyclerView()
        initializeViewModel()
        addViewModelObservers()

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

    override fun openAddMovieDialog() {
        Toast.makeText(requireContext(), "Open add movie#browse dialog", Toast.LENGTH_LONG).show()
    }

    private fun initializeRecyclerView() {
        val moviesLayoutManager = LinearLayoutManager(context)
        val moviesAdapter = MoviesRecyclerViewAdapter(listOf())

        binding.moviesRecyclerView.apply {
            layoutManager = moviesLayoutManager
            adapter = moviesAdapter
        }
    }

    private fun initializeViewModel() {
        moviesViewModel = ViewModelProvider(this, MoviesBrowseViewModelFactory())
            .get(MoviesBrowseViewModel::class.java)
    }

    private fun addViewModelObservers() {
        moviesViewModel.foundMovies.observe(viewLifecycleOwner, Observer {
            if (it != null)
                refreshData(it)
        })

        moviesViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        })

        moviesViewModel.hasAnyResults.observe(viewLifecycleOwner, Observer {
            when (it) {
                null -> {
                    showHomeBanner()
                }
                false -> {
                    showNoResultsBanner()
                }
                true -> {
                    showResults()
                }
            }
        })
    }

    private fun findMovies(query: String?) {
        moviesViewModel.findMovies(query)
    }

    private fun refreshData(data: List<MovieSimple>) {
        binding.moviesRecyclerView.adapter = MoviesRecyclerViewAdapter(data)
    }

    //#region UI changes
    private fun showHomeBanner() {
        binding.moviesRecyclerView.visibility = View.GONE
        binding.bannerMessage.visibility = View.VISIBLE
        binding.bannerMessage.text = getString(R.string.message_action_search_for_movies)
    }

    private fun showNoResultsBanner() {
        binding.moviesRecyclerView.visibility = View.GONE
        binding.bannerMessage.visibility = View.VISIBLE
        binding.bannerMessage.text = getString(R.string.message_no_results)
    }

    private fun showResults() {
        binding.moviesRecyclerView.visibility = View.VISIBLE
        binding.bannerMessage.visibility = View.GONE
        binding.bannerMessage.text = null
    }
    //#endregion
}