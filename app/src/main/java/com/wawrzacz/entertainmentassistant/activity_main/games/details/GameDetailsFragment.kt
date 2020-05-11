package com.wawrzacz.entertainmentassistant.activity_main.games.details

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
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
import com.wawrzacz.entertainmentassistant.activity_main.games.edition.GameEditionFragment
import com.wawrzacz.entertainmentassistant.data.enums.ItemSource
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.enums.PlayableSection
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.databinding.FragmentDetailsGameBinding

class GameDetailsFragment(private val gameId: String, private val parentView: View): DialogFragment() {

    private lateinit var binding: FragmentDetailsGameBinding
    private lateinit var detailsViewModel: GameDetailsViewModel
    private lateinit var mainActivity: MainActivity
    private var toPlayIconInitialized = false
    private var playedIconInitialized = false
    private var favIconInitialized = false

    private object DetailsMenu {
        lateinit var toPlay: MenuItem
        lateinit var played: MenuItem
        lateinit var favourites: MenuItem
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_details_game, container, false)

        initializeViewModel()
        observeViewModelChanges()
        addButtonListener()

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
        inflater.inflate(R.menu.menu_details_playable, menu)

        DetailsMenu.toPlay = menu.findItem(R.id.add_to_to_play)
        DetailsMenu.played = menu.findItem(R.id.add_to_played)
        DetailsMenu.favourites = menu.findItem(R.id.add_to_favourites)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> mainActivity.onBackPressed()
            R.id.add_to_to_play -> detailsViewModel.toggleItemSection(PlayableSection.TO_PLAY)
            R.id.add_to_played -> detailsViewModel.toggleItemSection(PlayableSection.PLAYED)
            R.id.add_to_favourites -> detailsViewModel.toggleItemSection(PlayableSection.FAVOURITES)
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
        detailsViewModel = ViewModelProvider(requireActivity().viewModelStore, GameDetailsViewModelFactory())
            .get(GameDetailsViewModel::class.java)
    }

    private fun observeViewModelChanges() {
        detailsViewModel.getDetailedItem(gameId).observe(viewLifecycleOwner, Observer { firebaseGame ->
            if (firebaseGame != null)
                populateViewWithData(firebaseGame)
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
            if (it?.id != gameId) hideDetailsContent()
            else showDetailsContent()
        })

        observeSectionChanges()
    }

    private fun observeSectionChanges() {
        detailsViewModel.isGameInToPlay(gameId).observe(viewLifecycleOwner, Observer {
            if (it == true) {
                setSectionIconFilled(PlayableSection.TO_PLAY)
                if (toPlayIconInitialized) showMessageAddedToSection(PlayableSection.TO_PLAY)
            }
            else {
                setSectionIconOutlined(PlayableSection.TO_PLAY)
                if (toPlayIconInitialized) showMessageRemovedFromSection(PlayableSection.TO_PLAY)
            }
            toPlayIconInitialized = true
        })

        detailsViewModel.isGameInPlayed(gameId).observe(viewLifecycleOwner, Observer {
            if (it == true) {
                setSectionIconFilled(PlayableSection.PLAYED)
                if (playedIconInitialized) showMessageAddedToSection(PlayableSection.PLAYED)
            }
            else {
                setSectionIconOutlined(PlayableSection.PLAYED)
                if (playedIconInitialized) showMessageRemovedFromSection(PlayableSection.PLAYED)
            }
            playedIconInitialized = true
        })

        detailsViewModel.isGameInFavourite(gameId).observe(viewLifecycleOwner, Observer {
            if (it == true) {
                setSectionIconFilled(PlayableSection.FAVOURITES)
                if (favIconInitialized) showMessageAddedToSection(PlayableSection.FAVOURITES)
            }
            else {
                setSectionIconOutlined(PlayableSection.FAVOURITES)
                if (favIconInitialized) showMessageRemovedFromSection(PlayableSection.FAVOURITES)
            }
            favIconInitialized = true
        })
    }

    private fun addButtonListener() {
        binding.fabEdit.setOnClickListener {
            openEditFragment()
        }
    }

    private fun openEditFragment() {
        val fragment = GameEditionFragment(parentView, true, detailsViewModel)
        (requireActivity() as MainActivity).supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(android.R.id.content, fragment, "CREATION_FRAGMENT")
        }.commit()
    }
    
    private fun populateViewWithData(game: DetailedItem) {
        setPosterBasedOnUrl(game, binding.poster)
        binding.title.text = game.title
        binding.typeIcon.setImageResource(R.drawable.gamepad_filled)
        binding.year.text = game.year
        binding.genre.text = game.genre
        binding.director.text = game.director
        binding.plot.text = game.plot
        binding.fabEdit.isVisible = game.source == ItemSource.FIREBASE
    }

    private fun setPosterBasedOnUrl(item: DetailedItem, view: ImageView) {
        if (item.posterUrl != "N/A" && !item.posterUrl.isBlank())
            Picasso.get().load(item.posterUrl).into(view)
    }

    private fun setSectionIconFilled(section: PlayableSection) {
        when (section) {
            PlayableSection.TO_PLAY -> DetailsMenu.toPlay.setIcon(R.drawable.bookmark_filled_24)
            PlayableSection.PLAYED -> DetailsMenu.played.setIcon(R.drawable.gamepad_filled)
            PlayableSection.FAVOURITES -> DetailsMenu.favourites.setIcon(R.drawable.heart_filled_24)
        }
    }

    private fun setSectionIconOutlined(section: PlayableSection) {
        when (section) {
            PlayableSection.TO_PLAY -> DetailsMenu.toPlay.setIcon(R.drawable.bookmark_outlined_24)
            PlayableSection.PLAYED -> DetailsMenu.played.setIcon(R.drawable.gamepad_outlined_24)
            PlayableSection.FAVOURITES -> DetailsMenu.favourites.setIcon(R.drawable.heart_outlined_24)
        }
    }

    private fun showMessageAddedToSection(section: PlayableSection) {
        var actionMessage = getString(R.string.error_general)
        var callback: () -> Unit = {}

        when (section) {
            PlayableSection.TO_PLAY -> {
                actionMessage = getString(R.string.action_message_added_to_to_play)
                callback = { detailsViewModel.toggleItemSection(PlayableSection.TO_PLAY) }
            }
            PlayableSection.PLAYED -> {
                actionMessage = getString(R.string.action_message_added_to_played)
                callback = { detailsViewModel.toggleItemSection(PlayableSection.PLAYED) }
            }
            PlayableSection.FAVOURITES -> {
                actionMessage = getString(R.string.action_message_added_to_favourites)
                callback = { detailsViewModel.toggleItemSection(PlayableSection.FAVOURITES) }
            }
        }
        showSectionSnackbar(actionMessage, callback)
    }

    private fun showMessageRemovedFromSection(section: PlayableSection) {
        var actionMessage = getString(R.string.error_general)
        var callback: () -> Unit = {}

        when (section) {
            PlayableSection.TO_PLAY -> {
                actionMessage = getString(R.string.action_message_removed_from_to_play)
                callback = { detailsViewModel.toggleItemSection(PlayableSection.TO_PLAY) }
            }
            PlayableSection.PLAYED -> {
                actionMessage = getString(R.string.action_message_removed_from_played)
                callback = { detailsViewModel.toggleItemSection(PlayableSection.PLAYED) }
            }
            PlayableSection.FAVOURITES -> {
                actionMessage = getString(R.string.action_message_removed_from_favourites)
                callback = { detailsViewModel.toggleItemSection(PlayableSection.FAVOURITES) }
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