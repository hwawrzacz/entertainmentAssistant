package com.wawrzacz.entertainmentassistant.activity_main.movies.watched

import android.os.Bundle
import android.util.Log
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
import com.wawrzacz.entertainmentassistant.data.Movie
import com.wawrzacz.entertainmentassistant.databinding.FragmentMoviesWatchedBinding

class MoviesWatchedFragment: Fragment(),
    MoviesListFragment {

    private lateinit var binding: FragmentMoviesWatchedBinding
    private lateinit var moviesViewModel: MoviesWatchedViewModel

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
                moviesViewModel.loadData(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
//                binding.textView.text = "Submitted watched: $query"
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
        val moviesAdapter = MoviesRecyclerViewAdapter(listOf())

        binding.moviesWatchedRecyclerView.apply {
            layoutManager = moviesLayoutManager
            adapter = moviesAdapter
        }
    }

    private fun initializeViewModel() {
        moviesViewModel = ViewModelProvider(this, MoviesWatchedViewModelFactory())
            .get(MoviesWatchedViewModel::class.java)
    }

    private fun addViewModelObservers() {
        moviesViewModel.moviesWatched.observe(viewLifecycleOwner, Observer {
            Log.i("schab", "datasize: ${it.size}")
            refreshData(it)
        })
    }

    private fun loadData() {
        moviesViewModel.loadData("")
    }

    private fun refreshData(data: List<Movie>) {
        binding.moviesWatchedRecyclerView.adapter = MoviesRecyclerViewAdapter(data)
    }
}