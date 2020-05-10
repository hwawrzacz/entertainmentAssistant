package com.wawrzacz.entertainmentassistant.activity_main.movies.creation

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
import com.wawrzacz.entertainmentassistant.activity_main.movies.details.MovieDetailsViewModel
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.response_statuses.FormValidationState
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.databinding.FragmentCreationMovieBinding

class MovieEditionFragment(val parentView: View, val isEdit: Boolean, val detailsViewModel: MovieDetailsViewModel?): DialogFragment() {
    private lateinit var binding: FragmentCreationMovieBinding
    private lateinit var movieEditionViewModel: MovieEditionViewModel
    private lateinit var mainActivity: MainActivity
    private var hasChanges = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_creation_movie, container, false)

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
                    movieEditionViewModel.updateMovie()
                else
                    movieEditionViewModel.createMovie()
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
        actionBar?.title = getString(R.string.title_create_movie)

        setHasOptionsMenu(true)
    }

    private fun initializeViewModel() {
        movieEditionViewModel = ViewModelProvider(viewModelStore, MovieEditionViewModelFactory())
            .get(MovieEditionViewModel::class.java)
    }

    private fun prepareView() {
        if (isEdit)
            binding.create.text = getString(R.string.action_save)
    }

    private fun observeViewModelChanges() {
        movieEditionViewModel.titleValidity.observe(viewLifecycleOwner, Observer {
            setTextViewError(binding.titleWrapper, it)
        })

        movieEditionViewModel.yearValidity.observe(viewLifecycleOwner, Observer {
            setTextViewError(binding.yearWrapper, it)
        })

        movieEditionViewModel.directorValidity.observe(viewLifecycleOwner, Observer {
            setTextViewError(binding.directorWrapper, it)
        })

        movieEditionViewModel.plotValidity.observe(viewLifecycleOwner, Observer {
            setTextViewError(binding.plotWrapper, it)
        })

        movieEditionViewModel.formValidity.observe(viewLifecycleOwner, Observer {
            binding.create.isEnabled = it
        })

        movieEditionViewModel.movieCreationStatus.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                when (it) {
                    ResponseStatus.IN_PROGRESS -> setAllInputsEnable(false)
                    ResponseStatus.SUCCESS -> {
                        setAllInputsEnable(true)
                        if (isEdit)
                            showSnackbarOnCreationSucceed()
                        else
                            showSnackbarOnUpdateSucceed()
                        mainActivity.onBackPressed()
                    }
                    ResponseStatus.ERROR -> {
                        setAllInputsEnable(true)
                        if (isEdit)
                            showSnackbarOnCreationFailed()
                        else
                            showSnackbarOnUpdateFailed()
                    }
                }
            }
        })

        movieEditionViewModel.hasChanges.observe(viewLifecycleOwner, Observer {
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
        movieEditionViewModel.setMovieId(item.id)
        binding.posterUrl.setText(item.posterUrl)
        binding.title.setText(item.title)
        binding.production.setText(item.production)
        binding.year.setText(item.year)
        binding.duration.setText(item.duration.replace(Regex("(min)| "), ""))
        binding.director.setText(item.director)
        binding.genre.setText(item.genre)
        binding.plot.setText(item.plot)
    }

    private fun addTextInputsListeners() {
        val onPosterUrlChanged: (String) -> Unit = { value -> movieEditionViewModel.onPosterUrlChanged(value) }
        val onTitleChanged: (String) -> Unit = { value -> movieEditionViewModel.onTitleChanged(value) }
        val onYearChanged: (String) -> Unit = { value -> movieEditionViewModel.onYearChanged(value) }
        val onDirectorChanged: (String) -> Unit = { value -> movieEditionViewModel.onDirectorChanged(value) }
        val onPlotChanged: (String) -> Unit = { value -> movieEditionViewModel.onPlotChanged(value) }
        val onProductionChanged: (String) -> Unit = { value -> movieEditionViewModel.onProductionChanged(value) }
        val onDurationChanged: (String) -> Unit = { value -> movieEditionViewModel.onDurationChanged(value) }
        val onGenreChanged: (String) -> Unit = { value -> movieEditionViewModel.onGenreChanged(value) }


        binding.posterUrl.addTextChangedListener(MyTextWatcher( onPosterUrlChanged ))
        binding.title.addTextChangedListener(MyTextWatcher( onTitleChanged ))
        binding.year.addTextChangedListener(MyTextWatcher( onYearChanged ))
        binding.director.addTextChangedListener(MyTextWatcher( onDirectorChanged ))
        binding.plot.addTextChangedListener(MyTextWatcher( onPlotChanged ))
        binding.production.addTextChangedListener(MyTextWatcher( onProductionChanged ))
        binding.duration.addTextChangedListener(MyTextWatcher( onDurationChanged ))
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
                movieEditionViewModel.updateMovie()
            else
                movieEditionViewModel.createMovie()
        }

        binding.cancel.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    
    private fun setAllInputsEnable(value: Boolean) {
        // TextBoxes
        binding.title.isEnabled = value
        binding.productionWrapper.isEnabled = value
        binding.yearWrapper.isEnabled = value
        binding.durationWrapper.isEnabled = value
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
        val message = getString(R.string.message_movie_created_successfully)
        val actionCallback = {}

        showSnackbarLong(parentView, message, null, actionCallback)
    }

    private fun showSnackbarOnCreationFailed() {
        val message = getString(R.string.error_creating_movie)
        val actionMessage = getString(R.string.action_retry)
        val actionCallback = { movieEditionViewModel.createMovie() }
        val view = binding.toolbar
        showSnackbarLong(view, message, actionMessage, actionCallback)
    }

    private fun showSnackbarOnUpdateSucceed() {
        val message = getString(R.string.message_movie_created_successfully)
        val actionCallback = {}

        showSnackbarLong(parentView, message, null, actionCallback)
    }

    private fun showSnackbarOnUpdateFailed() {
        val message = getString(R.string.error_creating_movie)
        val actionMessage = getString(R.string.action_retry)
        val actionCallback = { movieEditionViewModel.updateMovie() }
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