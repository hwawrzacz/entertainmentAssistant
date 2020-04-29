package com.wawrzacz.entertainmentassistant.activity_main.browse

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
import com.wawrzacz.entertainmentassistant.activity_main.details.DetailsFragment
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem
import com.wawrzacz.entertainmentassistant.databinding.FragmentBrowseBinding
import com.wawrzacz.entertainmentassistant.ui.adapters.BrowseListAdapter

class BrowseFragment: Fragment() {

    private lateinit var binding: FragmentBrowseBinding
    private lateinit var browseViewModel: BrowseViewModel
    private lateinit var moviesAdapter: BrowseListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_browse, container, false)

        setHasOptionsMenu(true)

        initializeViewModel()
        initializeRecyclerView()
        addViewModelObservers()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = (requireActivity() as AppCompatActivity)
        val actionBar = activity.supportActionBar
        actionBar?.title = getString(R.string.label_browse)
        actionBar?.setIcon(R.drawable.browse_24)
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
        moviesAdapter = BrowseListAdapter(browseViewModel)
        val moviesLayoutManager = LinearLayoutManager(context)

        binding.moviesRecyclerView.apply {
            layoutManager = moviesLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = moviesAdapter
        }
    }

    private fun initializeViewModel() {
        browseViewModel = ViewModelProvider(this, BrowseViewModelFactory())
            .get(BrowseViewModel::class.java)
    }

    private fun addViewModelObservers() {
        browseViewModel.foundItems.observe(viewLifecycleOwner, Observer {
            refreshData(it)
        })

        browseViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else {
                binding.progressBar.visibility = View.GONE
            }
        })

        browseViewModel.hasAnyResults.observe(viewLifecycleOwner, Observer {
            when (it) {
                null -> {
                    showBanner(getString(R.string.message_action_browse))
                    hideResults()
                    hideProgressBar()
                }
                false -> {
                    showBanner(getString(R.string.message_no_results))
                    hideResults()
                    hideProgressBar()
                }
                true -> {
                    hideBanner()
                    hideProgressBar()
                    showResults()
                }
            }
        })

        browseViewModel.isSuccessful.observe(viewLifecycleOwner, Observer {
            if (it !== null) {
                showBanner(getString(R.string.error_getting_data))
            }
        })

        browseViewModel.selectedItemId.observe(viewLifecycleOwner, Observer {
            openDetailsFragment(it)
        })
    }

    private fun findMovies(query: String?) {
        browseViewModel.findItems(query)
    }

    private fun refreshData(data: List<UniversalItem>?) {
        moviesAdapter.submitList(data)
    }

    private fun clearData() {
    }

    private fun openDetailsFragment(item: UniversalItem) {
        val activity = (requireActivity() as MainActivity)
        activity.hideKeyboard()


        var fragmentManager = requireActivity().supportFragmentManager
        val fragment = DetailsFragment(item)
        fragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_NONE)
            add(android.R.id.content, fragment)
            addToBackStack(fragment.tag)
        }.commit()
    }

    //#region UI changes
    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showBanner(string: String) {
        binding.bannerMessage.visibility = View.VISIBLE
        binding.bannerMessage.text = string
    }

    private fun hideBanner() {
        binding.bannerMessage.visibility = View.GONE
    }

    private fun showResults() {
        binding.moviesRecyclerView.visibility = View.VISIBLE
    }

    private fun hideResults() {
        binding.moviesRecyclerView.visibility = View.GONE
    }
    //#endregion
}