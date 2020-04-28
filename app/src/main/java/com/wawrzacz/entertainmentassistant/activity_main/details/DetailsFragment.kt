package com.wawrzacz.entertainmentassistant.activity_main.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
    }

    private fun setPosterBasedOnUrl(item: DetailedItem, view: ImageView) {
        if (item.posterURL == "N/A") {
            val imageResource: Int = when (item.type) {
                "series" -> R.mipmap.poster_default_series
                "game" -> R.mipmap.poster_default_game
                else -> R.mipmap.poster_default_movie
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
            "movie_24" -> R.drawable.movie_24
            "series" -> R.drawable.series_24
            else -> R.drawable.gamepad
        }
    }
}