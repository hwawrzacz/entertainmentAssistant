package com.wawrzacz.entertainmentassistant.activity_main.games.edition

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
import com.wawrzacz.entertainmentassistant.activity_main.games.details.GameDetailsViewModel
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.response_statuses.FormValidationState
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.databinding.FragmentEditionGameBinding

class GameEditionFragment(val parentView: View, val isEdit: Boolean, val detailsViewModel: GameDetailsViewModel?): DialogFragment() {
    private lateinit var binding: FragmentEditionGameBinding
    private lateinit var gameEditionViewModel: GameEditionViewModel
    private lateinit var mainActivity: MainActivity
    private var hasChanges = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_edition_game, container, false)

        initializeViewModel()
        prepareView()
        addTextInputsListeners()
        addButtonsListeners()
        observeViewModelChanges()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        mainActivity = requireActivity() as MainActivity
        initializeActionBar()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_creation, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> mainActivity.onBackPressed()
            R.id.menu_save_item -> {
                if (isEdit)
                    gameEditionViewModel.updateGame()
                else
                    gameEditionViewModel.createGame()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeActionBar() {
        val toolbar = binding.toolbar
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.arrow_back_24)
        actionBar?.title = getString(R.string.title_create_game)

        setHasOptionsMenu(true)
    }

    private fun initializeViewModel() {
        gameEditionViewModel = ViewModelProvider(viewModelStore, GameEditionViewModelFactory())
            .get(GameEditionViewModel::class.java)
    }

    private fun prepareView() {
        if (isEdit)
            binding.create.text = getString(R.string.action_save)
    }

    private fun observeViewModelChanges() {
        gameEditionViewModel.titleValidity.observe(viewLifecycleOwner, Observer {
            setTextViewError(binding.titleWrapper, it)
        })

        gameEditionViewModel.yearValidity.observe(viewLifecycleOwner, Observer {
            setTextViewError(binding.yearWrapper, it)
        })

        gameEditionViewModel.directorValidity.observe(viewLifecycleOwner, Observer {
            setTextViewError(binding.directorWrapper, it)
        })

        gameEditionViewModel.plotValidity.observe(viewLifecycleOwner, Observer {
            setTextViewError(binding.plotWrapper, it)
        })

        gameEditionViewModel.formValidity.observe(viewLifecycleOwner, Observer {
            binding.create.isEnabled = it
        })

        gameEditionViewModel.gameCreationStatus.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                when (it) {
                    ResponseStatus.IN_PROGRESS -> setAllInputsEnable(false)
                    ResponseStatus.SUCCESS -> {
                        setAllInputsEnable(true)
                        if (isEdit)
                            showSnackbarOnUpdateSucceed()
                        else
                            showSnackbarOnCreationSucceed()
                        mainActivity.onBackPressed()
                    }
                    ResponseStatus.ERROR -> {
                        setAllInputsEnable(true)
                        if (isEdit)
                            showSnackbarOnUpdateFailed()
                        else
                            showSnackbarOnCreationFailed()
                    }
                }
            }
        })

        gameEditionViewModel.hasChanges.observe(viewLifecycleOwner, Observer {
            hasChanges = it
        })

        if (isEdit) {
            detailsViewModel?.currentItem?.observe(viewLifecycleOwner, Observer {
                if (it != null)
                    populateFieldsWithData(it)
            })
        }
    }

    private fun populateFieldsWithData(item: DetailedItem) {
        gameEditionViewModel.setGameId(item.id)
        binding.posterUrl.setText(item.posterUrl)
        binding.title.setText(item.title)
        binding.year.setText(item.year)
        binding.director.setText(item.director)
        binding.genre.setText(item.genre)
        binding.plot.setText(item.plot)
    }

    private fun addTextInputsListeners() {
        val onPosterUrlChanged: (String) -> Unit = { value -> gameEditionViewModel.onPosterUrlChanged(value) }
        val onTitleChanged: (String) -> Unit = { value -> gameEditionViewModel.onTitleChanged(value) }
        val onYearChanged: (String) -> Unit = { value -> gameEditionViewModel.onYearChanged(value) }
        val onDirectorChanged: (String) -> Unit = { value -> gameEditionViewModel.onDirectorChanged(value) }
        val onPlotChanged: (String) -> Unit = { value -> gameEditionViewModel.onPlotChanged(value) }
        val onGenreChanged: (String) -> Unit = { value -> gameEditionViewModel.onGenreChanged(value) }


        binding.posterUrl.addTextChangedListener(MyTextWatcher( onPosterUrlChanged ))
        binding.title.addTextChangedListener(MyTextWatcher( onTitleChanged ))
        binding.year.addTextChangedListener(MyTextWatcher( onYearChanged ))
        binding.director.addTextChangedListener(MyTextWatcher( onDirectorChanged ))
        binding.plot.addTextChangedListener(MyTextWatcher( onPlotChanged ))
        binding.genre.addTextChangedListener(MyTextWatcher( onGenreChanged ))
    }

    private fun setTextViewError(view: TextInputLayout, state: FormValidationState) {
        when (state) {
            FormValidationState.VALID -> view.error = null
            FormValidationState.NOT_INITIALIZED -> view.error = null
            else -> view.error = state.value
        }
    }

    private fun addButtonsListeners() {
        binding.create.setOnClickListener {
            if (isEdit)
                gameEditionViewModel.updateGame()
            else
                gameEditionViewModel.createGame()
        }

        binding.cancel.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    
    private fun setAllInputsEnable(value: Boolean) {
        // TextBoxes
        binding.title.isEnabled = value
        binding.yearWrapper.isEnabled = value
        binding.directorWrapper.isEnabled = value
        binding.genreWrapper.isEnabled = value
        binding.plotWrapper.isEnabled = value
        
        // Checkboxes
//        binding.addToToWatch.isEnabled = value
//        binding.addToWatched.isEnabled = value
//        binding.addToFavourites.isEnabled = value

        // Buttons
        binding.create.isEnabled = value
        binding.cancel.isEnabled = value
    }

    private fun showSnackbarOnCreationSucceed() {
        val message = getString(R.string.message_game_created_successfully)
        val actionCallback = {}

        showSnackbarLong(parentView, message, null, actionCallback)
    }

    private fun showSnackbarOnCreationFailed() {
        val message = getString(R.string.error_creating_game)
        val actionMessage = getString(R.string.action_retry)
        val actionCallback = { gameEditionViewModel.createGame() }
        val view = binding.toolbar
        showSnackbarLong(view, message, actionMessage, actionCallback)
    }

    private fun showSnackbarOnUpdateSucceed() {
        val message = getString(R.string.message_game_updated_successfully)
        val actionCallback = {}

        showSnackbarLong(parentView, message, null, actionCallback)
    }

    private fun showSnackbarOnUpdateFailed() {
        val message = getString(R.string.error_updating_game)
        val actionMessage = getString(R.string.action_retry)
        val actionCallback = { gameEditionViewModel.updateGame() }
        val view = binding.toolbar
        showSnackbarLong(view, message, actionMessage, actionCallback)
    }

    private fun showSnackbarLong(view: View, message: String, actionMessage: String?, actionCallback: () -> Unit) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction(actionMessage) {
                actionCallback()
            }.show()
    }

    override fun dismiss() {
        if (hasChanges){
            MaterialAlertDialogBuilder(requireContext())
                .apply {
                    setTitle(R.string.title_unsaved_changes)
                    setMessage(R.string.confirmation_message_dismiss_unsaved_changes)
                    setPositiveButton(R.string.answer_yes) { _, _ -> run {
                            mainActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                            super.dismiss()
                        }
                    }
                    setNegativeButton(R.string.answer_no, null)
                }.show()
        }
        else super.dismiss()
    }

    class MyTextWatcher(var callback: (value: String) -> Unit): TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            callback(s.toString())
        }
    }
}