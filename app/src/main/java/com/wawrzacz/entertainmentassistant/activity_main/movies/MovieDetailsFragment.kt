package com.wawrzacz.entertainmentassistant.activity_main.movies

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
import com.wawrzacz.entertainmentassistant.activity_main.details.DetailsViewModel
import com.wawrzacz.entertainmentassistant.activity_main.details.DetailsViewModelFactory
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection
import com.wawrzacz.entertainmentassistant.databinding.FragmentDetailsMovieBinding

class MovieDetailsFragment(private val movieId: String): DialogFragment() {

    private lateinit var binding: FragmentDetailsMovieBinding
    private lateinit var detailsViewModel: DetailsViewModel
    private lateinit var mainActivity: MainActivity
    private var toWatchIconInitialized = false
    private var watchedIconInitialized = false
    private var favIconInitialized = false
    
    private object DetailsMenu {
        lateinit var toWatch: MenuItem
        lateinit var watched: MenuItem
        lateinit var favourites: MenuItem
    }

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

        DetailsMenu.toWatch = menu.findItem(R.id.add_to_to_watch)
        DetailsMenu.watched = menu.findItem(R.id.add_to_watched)
        DetailsMenu.favourites = menu.findItem(R.id.add_to_favourites)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> mainActivity.onBackPressed()
            R.id.add_to_to_watch -> detailsViewModel.toggleItemSection(WatchableSection.TO_WATCH)
            R.id.add_to_watched -> detailsViewModel.toggleItemSection(WatchableSection.WATCHED)
            R.id.add_to_favourites -> detailsViewModel.toggleItemSection(WatchableSection.FAVOURITES)
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
        detailsViewModel.getDetailedItem(movieId).observe(viewLifecycleOwner, Observer { firebaseMovie ->
            if (firebaseMovie != null)
                populateViewWithData(firebaseMovie)
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

        detailsViewModel.currentItem.observe(viewLifecycleOwner, Observer {
            if (it?.id != movieId) hideDetailsContent()
            else showDetailsContent()
        })

        observeSectionChanges()
    }

    private fun observeSectionChanges() {
        detailsViewModel.isMovieInToWatch(movieId).observe(viewLifecycleOwner, Observer {
            if (it == true) {
                setSectionIconFilled(WatchableSection.TO_WATCH)
                if (toWatchIconInitialized) showMessageAddedToSection(WatchableSection.TO_WATCH)
            }
            else {
                setSectionIconOutlined(WatchableSection.TO_WATCH)
                if (toWatchIconInitialized) showMessageRemovedFromSection(WatchableSection.TO_WATCH)
            }
            toWatchIconInitialized = true
        })

        detailsViewModel.isMovieInWatched(movieId).observe(viewLifecycleOwner, Observer {
            if (it == true) {
                setSectionIconFilled(WatchableSection.WATCHED)
                if (watchedIconInitialized) showMessageAddedToSection(WatchableSection.WATCHED)
            }
            else {
                setSectionIconOutlined(WatchableSection.WATCHED)
                if (watchedIconInitialized) showMessageRemovedFromSection(WatchableSection.WATCHED)
            }
            watchedIconInitialized = true
        })

        detailsViewModel.isMovieInFavourite(movieId).observe(viewLifecycleOwner, Observer {
            if (it == true) {
                setSectionIconFilled(WatchableSection.FAVOURITES)
                if (favIconInitialized) showMessageAddedToSection(WatchableSection.FAVOURITES)
            }
            else {
                setSectionIconOutlined(WatchableSection.FAVOURITES)
                if (favIconInitialized) showMessageRemovedFromSection(WatchableSection.FAVOURITES)
            }
            favIconInitialized = true
        })
    }
    
    private fun populateViewWithData(movie: DetailedItem) {
        setPosterBasedOnUrl(movie, binding.poster)
        binding.title.text = movie.title
        binding.production.text = movie.production
        binding.icon.setImageResource(R.drawable.movie_24)
        binding.year.text = movie.year
        binding.runtime.text = movie.runtime
        binding.genre.text = movie.genre
        binding.director.text = movie.director
        binding.plot.text = movie.plot
    }

    private fun setPosterBasedOnUrl(item: DetailedItem, view: ImageView) {
        if (item.posterUrl != "N/A")
            Picasso.get().load(item.posterUrl).into(view)
    }

    private fun setSectionIconFilled(section: WatchableSection) {
        when (section) {
            WatchableSection.WATCHED -> DetailsMenu.watched.setIcon(R.drawable.eye_filled_24)
            WatchableSection.TO_WATCH -> DetailsMenu.toWatch.setIcon(R.drawable.bookmark_filled_24)
            WatchableSection.FAVOURITES -> DetailsMenu.favourites.setIcon(R.drawable.heart_filled_24)
        }
    }

    private fun setSectionIconOutlined(section: WatchableSection) {
        when (section) {
            WatchableSection.WATCHED -> DetailsMenu.watched.setIcon(R.drawable.eye_outlined_24)
            WatchableSection.TO_WATCH -> DetailsMenu.toWatch.setIcon(R.drawable.bookmark_outlined_24)
            WatchableSection.FAVOURITES -> DetailsMenu.favourites.setIcon(R.drawable.heart_outlined_24)
        }
    }

    private fun showMessageAddedToSection(section: WatchableSection) {
        var actionMessage = getString(R.string.error_general)
        var callback: () -> Unit = {}

        when (section) {
            WatchableSection.WATCHED -> {
                actionMessage = getString(R.string.action_message_added_to_watched)
                callback = { detailsViewModel.toggleItemSection(WatchableSection.WATCHED) }
            }
            WatchableSection.TO_WATCH -> {
                actionMessage = getString(R.string.action_message_added_to_to_watch)
                callback = { detailsViewModel.toggleItemSection(WatchableSection.TO_WATCH) }
            }
            WatchableSection.FAVOURITES -> {
                actionMessage = getString(R.string.action_message_added_to_favourites)
                callback = { detailsViewModel.toggleItemSection(WatchableSection.FAVOURITES) }
            }
        }
        showSectionSnackbar(actionMessage, callback)
    }

    private fun showMessageRemovedFromSection(section: WatchableSection) {
        var actionMessage = getString(R.string.error_general)
        var callback: () -> Unit = {}

        when (section) {
            WatchableSection.WATCHED -> {
                actionMessage = getString(R.string.action_message_removed_from_watched)
                callback = { detailsViewModel.toggleItemSection(WatchableSection.WATCHED) }
            }
            WatchableSection.TO_WATCH -> {
                actionMessage = getString(R.string.action_message_removed_from_to_watch)
                callback = { detailsViewModel.toggleItemSection(WatchableSection.TO_WATCH) }
            }
            WatchableSection.FAVOURITES -> {
                actionMessage = getString(R.string.action_message_removed_from_favourites)
                callback = { detailsViewModel.toggleItemSection(WatchableSection.FAVOURITES) }
            }
        }
        showSectionSnackbar(actionMessage, callback)
    }

    private fun hideDetailsContent() {
        binding.detailsContainer.visibility = View.GONE
    }

    private fun showDetailsContent() {
        binding.detailsContainer.visibility = View.VISIBLE
    }

    private fun removeObservers() {
        detailsViewModel.isSuccessful.removeObservers(viewLifecycleOwner)
        detailsViewModel.isLoading.removeObservers(viewLifecycleOwner)
    }

    override fun onDismiss(dialog: DialogInterface) {
        removeObservers()
        removeObservers()
        super.onDismiss(dialog)
    }

    private fun showSectionSnackbar(message: String, callback: () -> Unit) {
        val undoText = getString(R.string.action_undo)
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            .setAction(undoText) {
                callback()
            }
            .show()
    }
}