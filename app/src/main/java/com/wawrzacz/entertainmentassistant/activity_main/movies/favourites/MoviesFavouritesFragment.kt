package com.wawrzacz.entertainmentassistant.activity_main.movies.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.browse.BrowseViewModel
import com.wawrzacz.entertainmentassistant.activity_main.movies.MoviesListFragment
import com.wawrzacz.entertainmentassistant.databinding.FragmentMoviesFavouritesBinding
import com.wawrzacz.entertainmentassistant.ui.adapters.BrowseListAdapter

class MoviesFavouritesFragment: Fragment(),
    MoviesListFragment {

    private lateinit var binding: FragmentMoviesFavouritesBinding
    private lateinit var recyclerView: RecyclerView
    // TODO: replaec browse view model
    private val moviesAdapter = BrowseListAdapter(BrowseViewModel())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_movies_favourites, container, false)

        setHasOptionsMenu(true)
        initializeRecyclerView()
        loadRecyclerViewMockData()

        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchItem = menu.findItem(R.id.nav_search)
        val searchView = searchItem.actionView as SearchView?

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                Toast.makeText(context,"Search favourites: $newText", Toast.LENGTH_LONG).show()
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
//                binding.textView.text = "Submitted: favourites: $query"
                return true
            }
        })
    }

    override fun openAddMovieDialog() {
        Toast.makeText(requireContext(), "Open add movie_24#favourites dialog", Toast.LENGTH_LONG).show()
    }

    private fun initializeRecyclerView() {
        val viewManager = LinearLayoutManager(context)

        recyclerView = binding.moviesRecyclerView.apply {
            layoutManager = viewManager
            adapter = moviesAdapter
        }
    }

    private fun loadRecyclerViewMockData() {
//        val data = getMockData()
//        moviesAdapter.data = data
    }
}