package com.wawrzacz.entertainmentassistant.activity_main.details

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem
import com.wawrzacz.entertainmentassistant.databinding.FragmentDetailsBinding
import com.wawrzacz.entertainmentassistant.ui.adapters.BrowseListAdapter
import java.util.*

class DetailsFragment(private val item: UniversalItem): DialogFragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var detailsViewModel: DetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_details, container, false)

        setViewsVisibilityBasedOnItemType()
        initializeViewModel()
        observeViewModelChanges()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initializeActionBar()
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Toast.makeText(context, "Back pressed", Toast.LENGTH_LONG).show()
                dismiss()
                activity?.onBackPressed()
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

    private fun setViewsVisibilityBasedOnItemType() {
        when (item.type.toLowerCase()) {
            "game" -> {
                binding.production.visibility = View.GONE
                binding.runtime.visibility = View.GONE
                binding.seasons.visibility = View.GONE
                binding.labelWriter.visibility = View.GONE
                binding.writer.visibility = View.GONE
            }
            "movie" -> {
                binding.seasons.visibility = View.GONE
                binding.labelWriter.visibility = View.GONE
                binding.writer.visibility = View.GONE
            }
            "series" -> {
                binding.production.visibility = View.GONE
                binding.runtime.visibility = View.GONE
                binding.labelDirector.visibility = View.GONE
                binding.director.visibility = View.GONE
            }
        }
    }

    private fun initializeViewModel() {
        detailsViewModel = ViewModelProvider(viewModelStore, DetailsViewModelFactory())
            .get(DetailsViewModel::class.java)
    }

    private fun observeViewModelChanges() {
        detailsViewModel.getItem(item.id).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                setPosterBasedOnUrl(it, binding.poster)
                binding.title.text = it.title
                binding.production.text = it.production
                binding.icon.setImageResource(getTypeDrawable(it.type))
                binding.year.text = it.year
                binding.runtime.text = it.runtime
                binding.seasons.text = adjustSeasonsText(it.totalSeasons)
                binding.genre.text = it.genre
                binding.director.text = it.director
                binding.writer.text = it.writer
                binding.plot.text = it.plot
            }
        })

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
        if (item.posterURL == "N/A") {
            val imageResource: Int = when (item.type) {
                "movie" -> R.mipmap.poster_default_movie
                "series" -> R.mipmap.poster_default_series
                else -> R.mipmap.poster_default_game
            }
            view.setImageResource(imageResource)
        } else Picasso.get().load(item.posterURL).into(view)
    }

    private fun adjustSeasonsText(text: String?): String {
        var result = text.toString()
        if (result == "1") result = "$text season"
        else if (result != "N/A") result = "$text seasons"

        return result
    }

    private fun getTypeDrawable(value: String): Int {
        return when (value.toLowerCase(Locale.getDefault())) {
            "movie" -> R.drawable.movie_24
            "series" -> R.drawable.series_24
            else -> R.drawable.gamepad
        }
    }
}