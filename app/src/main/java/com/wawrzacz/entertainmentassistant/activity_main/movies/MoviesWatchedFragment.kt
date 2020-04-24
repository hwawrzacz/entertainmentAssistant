package com.wawrzacz.entertainmentassistant.activity_main.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.databinding.FragmentMoviesWatchedBinding

class MoviesWatchedFragment: Fragment() {

    private lateinit var binding: FragmentMoviesWatchedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_movies_watched, container, false)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val searchItem = menu.findItem(R.id.nav_search)
        val searchView = searchItem?.actionView as SearchView?

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                binding.textView.text = "Search watched: $newText"
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.textView.text = "Submitted watched: $query"
                return false
            }
        })

        super.onPrepareOptionsMenu(menu)
    }
}