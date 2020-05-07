package com.wawrzacz.entertainmentassistant.activity_main.movies.movie_creation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
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
import com.wawrzacz.entertainmentassistant.data.errors.FormValidationState
import com.wawrzacz.entertainmentassistant.databinding.FragmentCretionMovieBinding

class MovieCreationFragment: DialogFragment() {
    private lateinit var binding: FragmentCretionMovieBinding
    private lateinit var viewModel: MovieCreationViewModel
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_cretion_movie, container, false)

        initializeViewModel()
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> mainActivity.onBackPressed()
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
        viewModel = ViewModelProvider(viewModelStore, MovieCreationViewModelFactory())
            .get(MovieCreationViewModel::class.java)
    }

    private fun observeViewModelChanges() {
        viewModel.titleValidity.observe(viewLifecycleOwner, Observer {
            setTextViewError(binding.titleWrapper, it)
        })

        viewModel.yearValidity.observe(viewLifecycleOwner, Observer {
            setTextViewError(binding.yearWrapper, it)
        })

        viewModel.directorValidity.observe(viewLifecycleOwner, Observer {
            setTextViewError(binding.directorWrapper, it)
        })

        viewModel.plotValidity.observe(viewLifecycleOwner, Observer {
            setTextViewError(binding.plotWrapper, it)
        })

        viewModel.formValidity.observe(viewLifecycleOwner, Observer {
            binding.create.isEnabled = it
        })
    }

    private fun setTextViewError(view: TextInputLayout, state: FormValidationState) {
        when (state) {
            FormValidationState.VALID -> view.error = null
            FormValidationState.NOT_INITIALIZED -> view.error = null
            else -> view.error = state.value
        }
    }


    private fun addTextInputsListeners() {
        val titleValidator: (String) -> Unit = { value -> viewModel.validateTitle(value) }
        val yearValidator: (String) -> Unit = { value -> viewModel.validateYear(value) }
        val directorValidator: (String) -> Unit = { value -> viewModel.validateDirector(value) }
        val plotValidator: (String) -> Unit = { value -> viewModel.validatePlot(value) }

        binding.title.addTextChangedListener(MyTextWatcher( titleValidator ))
        binding.year.addTextChangedListener(MyTextWatcher( yearValidator ))
        binding.director.addTextChangedListener(MyTextWatcher( directorValidator ))
        binding.plot.addTextChangedListener(MyTextWatcher( plotValidator ))
    }

    class MyTextWatcher(var callback: (value: String) -> Unit): TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            callback(s.toString())
        }
    }

    private fun addButtonsListeners() {
        binding.create.setOnClickListener {
            Toast.makeText(requireContext(), "Create new movie", Toast.LENGTH_LONG).show()
        }

        binding.cancel.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun dismiss() {
        MaterialAlertDialogBuilder(requireContext())
            .apply {
                setTitle(R.string.title_unsaved_changes)
                setMessage(R.string.confirmation_message_dismiss_unsaved_changes)
                setPositiveButton(R.string.answer_yes) { _, _ -> run {

                        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                        super.dismiss()
                    }
                }
                setNegativeButton(R.string.answer_no, null)
            }.show()
    }
}