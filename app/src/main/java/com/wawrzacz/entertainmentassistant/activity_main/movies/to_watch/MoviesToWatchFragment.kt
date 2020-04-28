package com.wawrzacz.entertainmentassistant.activity_main.movies.to_watch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.movies.MoviesListFragment
import com.wawrzacz.entertainmentassistant.databinding.FragmentMoviesToWatchBinding

class MoviesToWatchFragment: Fragment(),
    MoviesListFragment {

    private lateinit var binding: FragmentMoviesToWatchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_movies_to_watch, container, false)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchItem = menu.findItem(R.id.nav_search)
        val searchView = searchItem?.actionView as SearchView?

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean { return false }
        })

        return super.onPrepareOptionsMenu(menu)
    }

    override fun openAddMovieDialog() {
        Toast.makeText(requireContext(), "Open add movie_24#toWatch dialog", Toast.LENGTH_LONG).show()
    }


}