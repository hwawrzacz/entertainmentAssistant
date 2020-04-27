package com.wawrzacz.entertainmentassistant.activity_main.details

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem
import com.wawrzacz.entertainmentassistant.databinding.FragmentDetailsBinding
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_details.*
import java.util.*

class DetailsFragment(private val item: UniversalItem): DialogFragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var detailsViewModel: DetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(STYLE_NO_FRAME, android.R.style.ThemeOverlay)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_details, container, false)

        setViewsVisibilityBasedOnItemType()
        initializeViewModel()
        observeViewModelChanges()

        return binding.root
    }

    private fun setViewsVisibilityBasedOnItemType() {
        when (item.type.toLowerCase()) {
            "game" -> {
                binding.production.visibility = View.GONE
                binding.runtime.visibility = View.GONE
                binding.seasons.visibility = View.GONE
                binding.labelDirector.visibility = View.GONE
                binding.director.visibility = View.GONE
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
                Picasso.get().load(item.posterURL).into(binding.poster)
                binding.title.text = it.title
                binding.production.text = it.production
                binding.icon.setImageResource(getTypeDrawable(it.type))
                binding.year.text = it.year
                binding.runtime.text = it.runtime
                binding.seasons.text = it.totalSeasons.toString()
                binding.genre.text = it.genre
                binding.director.text = it.director
                binding.writer.text = it.writer
                binding.plot.text = it.plot
            }
        })
    }

    private fun getTypeDrawable(value: String): Int {
        return when (value.toLowerCase(Locale.getDefault())) {
            "movie" -> R.drawable.movies_rounded
            "series" -> R.drawable.series_24
            else -> R.drawable.gamepad
        }
    }
}