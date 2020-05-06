package com.wawrzacz.entertainmentassistant.activity_main.movies.new_movie

import android.content.DialogInterface
import android.os.Bundle
import android.security.ConfirmationPrompt
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.databinding.FragmentCretionMovieBinding

class MovieCreationFragment(): DialogFragment() {
    private lateinit var binding: FragmentCretionMovieBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_cretion_movie, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        mainActivity = requireActivity() as MainActivity
        initializeActionBar()
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
    }

    private fun observeViewModelChanges() {

    }

    private fun observeSectionChanges() {
    }

    private fun populateViewWithData(movie: DetailedItem) {
    }

    override fun dismiss() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setMessage(R.string.confirmation_message_dismiss_unsaved_changes)
            setTitle(R.string.title_unsaved_changes)
            setNegativeButton(R.string.answer_no, null)
            setPositiveButton(R.string.answer_yes) { _, _ -> super.dismiss() }
        }.show()
    }
}