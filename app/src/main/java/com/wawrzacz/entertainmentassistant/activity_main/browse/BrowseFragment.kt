package com.wawrzacz.entertainmentassistant.activity_main.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
import com.wawrzacz.entertainmentassistant.activity_main.movies.adapters.MoviesRecyclerViewAdapter
import com.wawrzacz.entertainmentassistant.data.model.MovieSimple
import com.wawrzacz.entertainmentassistant.databinding.FragmentBrowseBinding

class BrowseFragment: Fragment() {

    private lateinit var binding: FragmentBrowseBinding
    private lateinit var moviesViewModel: BrowseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_browse, container, false)

        setHasOptionsMenu(true)

        initializeRecyclerView()
        initializeViewModel()
        addViewModelObservers()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as MainActivity).setActionBarTitle(getString(R.string.label_browse))
        (requireActivity() as MainActivity).setActionBarIcon(R.drawable.browse_24)
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
        val moviesAdapter = MoviesRecyclerViewAdapter(listOf())

        binding.moviesRecyclerView.apply {
            layoutManager = moviesLayoutManager
            adapter = moviesAdapter
        }
    }

    private fun initializeViewModel() {
        moviesViewModel = ViewModelProvider(this, BrowseViewModelFactory())
            .get(BrowseViewModel::class.java)
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
        binding.bannerMessage.text = getString(R.string.message_action_browse)
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