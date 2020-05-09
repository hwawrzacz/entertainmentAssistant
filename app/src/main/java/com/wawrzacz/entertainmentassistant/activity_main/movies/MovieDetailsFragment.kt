package com.wawrzacz.entertainmentassistant.activity_main.movies

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
import com.wawrzacz.entertainmentassistant.activity_main.details.DetailsViewModel
import com.wawrzacz.entertainmentassistant.activity_main.details.DetailsViewModelFactory
import com.wawrzacz.entertainmentassistant.activity_main.movies.movie_creation.MovieCreationFragment
import com.wawrzacz.entertainmentassistant.data.enums.ItemSource
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
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
        addButtonsListeners()

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
        actionBar?.title = getString(R.string.title_details)

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

        detailsViewModel.responseStatus.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                when (it) {
                    ResponseStatus.NOT_INITIALIZED -> {
                        hideProgressBar()
                        hideDetailsContent()
                    }
                    ResponseStatus.IN_PROGRESS -> {
                        hideDetailsContent()
                        hideBanner()
                        showProgressBar()
                    }
                    ResponseStatus.SUCCESS -> {
                        hideProgressBar()
                        hideBanner()
                        showDetailsContent()
                    }
                    ResponseStatus.ERROR -> {
                        hideProgressBar()
                        hideDetailsContent()
                        showBanner(getString(R.string.error_getting_data))
                    }
                    ResponseStatus.NO_RESULT -> {
                        hideProgressBar()
                        hideDetailsContent()
                        showBanner(getString(R.string.message_no_results))
                    }
                }
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

    private fun addButtonsListeners() {
        binding.edit.setOnClickListener {
            openEditFragment()
        }
    }

    private fun openEditFragment() {
        val fragment = MovieCreationFragment(binding.detailsContainer, true, detailsViewModel)
        (requireActivity() as MainActivity).supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(android.R.id.content, fragment, "CREATION_FRAGMENT")
        }.commit()
    }
    
    private fun populateViewWithData(movie: DetailedItem) {
        setPosterBasedOnUrl(movie, binding.poster)
        binding.title.text = movie.title
        binding.production.text = movie.production
        binding.typeIcon.setImageResource(R.drawable.movie_24)
        binding.year.text = movie.year
        binding.duration.text = movie.duration
        binding.genre.text = movie.genre
        binding.director.text = movie.director
        binding.plot.text = movie.plot
        binding.edit.isVisible = movie.source == ItemSource.FIREBASE
    }

    private fun setPosterBasedOnUrl(item: DetailedItem, view: ImageView) {
        if (item.posterUrl == "N/A" || item.posterUrl.isNullOrBlank()) {
            val imageResource: Int = when (item.type) {
                "series" -> R.mipmap.poster_default_series
                "game" -> R.mipmap.poster_default_game
                else -> R.mipmap.poster_default_movie
            }
            binding.poster.setImageResource(imageResource)
        } else Picasso.get().load(item.posterUrl).into(view)
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

    private fun showBanner(message: String) {
        binding.message.text = message
        binding.message.visibility = View.VISIBLE
    }

    private fun hideBanner() {
        binding.message.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showDetailsContent() {
        binding.detailsContainer.visibility = View.VISIBLE
    }

    private fun hideDetailsContent() {
        binding.detailsContainer.visibility = View.GONE
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