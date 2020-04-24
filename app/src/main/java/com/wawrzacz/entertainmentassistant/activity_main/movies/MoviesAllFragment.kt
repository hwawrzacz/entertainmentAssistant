package com.wawrzacz.entertainmentassistant.activity_main.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.movies.adapters.MoviesRecyclerViewAdapter
import com.wawrzacz.entertainmentassistant.data.Movie
import com.wawrzacz.entertainmentassistant.databinding.FragmentMoviesAllBinding

class MoviesAllFragment: Fragment() {

    private lateinit var binding: FragmentMoviesAllBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var moviesAdapter: MoviesRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_movies_all, container, false)

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
//                binding.textView.text = "Search favourites: $newText"
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
//                binding.textView.text = "Submitted: favourites: $query"
                return true
            }
        })
    }

    private fun initializeRecyclerView() {
        val viewManager = LinearLayoutManager(context)
        moviesAdapter = MoviesRecyclerViewAdapter(listOf())

        recyclerView = binding.moviesRecyclerView.apply {
            layoutManager = viewManager
            adapter = moviesAdapter
        }
    }

    private fun loadRecyclerViewMockData() {
        val data = getMockData()
        moviesAdapter.data = data
    }

    private fun getMockData(): List<Movie> {
        return mutableListOf(
            Movie(null, "Pulp fiction", "asd", 179, "Quentin Tarantino", "USA", 1994, true),
            Movie(null, "Fast and Furious", "asd", 125, "Jet Lee", "USA", 1999, true),
            Movie(null, "Enemy", "asd", 142, "Johny Random", "Canada", 2007, false),
            Movie(null, "Frozen", "asd", 117, "Disney", "USA", 2015, false),
            Movie(null, "Rambo", "asd", 124, "Nevermind", "USA", 1997, false),
            Movie(null, "Breaking bad", "asd", 58, "Do not know", "USA", 2010, true)
        )
    }
}