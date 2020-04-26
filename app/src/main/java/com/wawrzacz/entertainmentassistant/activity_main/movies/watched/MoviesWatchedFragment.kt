package com.wawrzacz.entertainmentassistant.activity_main.movies.watched

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
import com.wawrzacz.entertainmentassistant.activity_main.movies.MoviesListFragment
import com.wawrzacz.entertainmentassistant.data.model.MovieSimple
import com.wawrzacz.entertainmentassistant.databinding.FragmentMoviesWatchedBinding
import com.wawrzacz.entertainmentassistant.ui.adapters.MovieListAdapter

class MoviesWatchedFragment: Fragment(),
    MoviesListFragment {

    private lateinit var binding: FragmentMoviesWatchedBinding
    private lateinit var moviesViewModel: MoviesWatchedViewModel
    private val moviesAdapter = MovieListAdapter()

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

        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchItem = menu.findItem(R.id.nav_search)
        val searchView = searchItem?.actionView as SearchView?

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()){
                    findMovies(newText)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
        })

        super.onPrepareOptionsMenu(menu)
    }

    override fun openAddMovieDialog() {
        Toast.makeText(requireContext(), "Open add movie#watched dialog", Toast.LENGTH_LONG).show()
    }

    private fun initializeRecyclerView() {
        val moviesLayoutManager = LinearLayoutManager(context)

        binding.moviesRecyclerView.apply {
            layoutManager = moviesLayoutManager
            adapter = moviesAdapter
        }
    }

    private fun initializeViewModel() {
        moviesViewModel = ViewModelProvider(this, MoviesWatchedViewModelFactory())
            .get(MoviesWatchedViewModel::class.java)
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
    }

    private fun findMovies(query: String) {
        moviesViewModel.findMovies(query)
    }

    private fun loadData() {
        moviesViewModel.loadData()
    }

    private fun refreshData(data: List<MovieSimple>) {
        moviesAdapter.submitList(data)
    }
}