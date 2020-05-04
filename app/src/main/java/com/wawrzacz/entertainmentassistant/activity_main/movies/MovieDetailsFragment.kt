package com.wawrzacz.entertainmentassistant.activity_main.movies

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
import com.wawrzacz.entertainmentassistant.data.enums.Section
import com.wawrzacz.entertainmentassistant.databinding.FragmentDetailsMovieBinding

class MovieDetailsFragment(private val movieId: String): DialogFragment() {

    private lateinit var binding: FragmentDetailsMovieBinding
    private lateinit var detailsViewModel: DetailsViewModel
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_details_movie, container, false)

        initializeViewModel()
        observeViewModelChanges()

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
                detailsViewModel.addItemToFirebaseDatabase(Section.WATCHED)
            }
            R.id.add_to_to_watch -> {
                makeToastLong("Add to to watch")
                detailsViewModel.addItemToFirebaseDatabase(Section.TO_WATCH)
            }
            R.id.add_to_favourites -> {
                makeToastLong("Add to favourites")
                detailsViewModel.addItemToFirebaseDatabase(Section.FAVOURITES)
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
        detailsViewModel = ViewModelProvider(requireActivity().viewModelStore, DetailsViewModelFactory())
            .get(DetailsViewModel::class.java)
    }

    private fun observeViewModelChanges() {
        detailsViewModel.getDetailedItem(movieId).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                setPosterBasedOnUrl(it, binding.poster)
                binding.title.text = it.title
                binding.production.text = it.production
                binding.icon.setImageResource(R.drawable.movie_24)
                binding.year.text = it.year
                binding.runtime.text = it.runtime
                binding.genre.text = it.genre
                binding.director.text = it.director
                binding.plot.text = it.plot
            }
        })

        detailsViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        })

        detailsViewModel.isSuccessful.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.message.visibility = View.GONE
                binding.detailsContainer.visibility = View.VISIBLE
            }
            else {
                binding.message.visibility = View.VISIBLE
                binding.detailsContainer.visibility = View.GONE
            }
        })

    }

    private fun setPosterBasedOnUrl(item: DetailedItem, view: ImageView) {
        if (item.posterUrl != "N/A")
            Picasso.get().load(item.posterUrl).into(view)
    }

    private fun makeToastLong(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}