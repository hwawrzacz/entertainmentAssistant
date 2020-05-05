package com.wawrzacz.entertainmentassistant.activity_main.series

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
import com.wawrzacz.entertainmentassistant.activity_main.details.DetailsViewModel
import com.wawrzacz.entertainmentassistant.activity_main.details.DetailsViewModelFactory
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.databinding.FragmentDetailsSeriesBinding

class SeriesDetailsFragment(private val id: String): DialogFragment() {

    private lateinit var binding: FragmentDetailsSeriesBinding
    private lateinit var detailsViewModel: DetailsViewModel
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_details_series, container, false)

        initializeViewModel()
//        observeViewModelChanges()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        mainActivity = requireActivity() as MainActivity
        initializeActionBar()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_details_watchable, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mainActivity.onBackPressed()
                mainActivity.initializeActionBar()
            }
            R.id.add_to_watched -> {
                makeToastLong("Add to watched")
            }
            R.id.add_to_to_watch -> {
                makeToastLong("Add to to watch")
            }
            R.id.add_to_favourites -> {
                makeToastLong("Add to favourites")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeActionBar() {
        val toolbar = binding.detailsToolbar
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.arrow_back_24)
        actionBar?.title = "Details"

        setHasOptionsMenu(true)
    }

    private fun initializeViewModel() {
        detailsViewModel = ViewModelProvider(viewModelStore,
            DetailsViewModelFactory()
        )
            .get(DetailsViewModel::class.java)
    }

    private fun observeViewModelChanges() {
        detailsViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
                binding.detailsContainer.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.detailsContainer.visibility = View.VISIBLE
            }
        })

        detailsViewModel.isSuccessful.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.message.visibility = View.GONE
            } else {
                binding.message.visibility = View.VISIBLE
            }
        })
    }

    private fun setPosterBasedOnUrl(item: DetailedItem, view: ImageView) {
        if (item.posterUrl != "N/A")
            Picasso.get().load(item.posterUrl).into(view)
    }

    private fun adjustSeasonsText(text: String?): String {
        var result = text.toString()
        if (result == "1") result = "$text season"
        else if (result != "N/A") result = "$text seasons"

        return result
    }

    private fun makeToastLong(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}