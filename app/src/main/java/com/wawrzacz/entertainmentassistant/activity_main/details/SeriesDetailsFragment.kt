package com.wawrzacz.entertainmentassistant.activity_main.details

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.MainActivity
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem
import com.wawrzacz.entertainmentassistant.databinding.FragmentDetailsSeriesBinding
import java.util.*

class SeriesDetailsFragment(private val item: UniversalItem): DialogFragment() {

    private lateinit var binding: FragmentDetailsSeriesBinding
    private lateinit var detailsViewModel: DetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_details_movie, container, false)

        initializeViewModel()
        setButtonListeners()
        observeViewModelChanges()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initializeActionBar()
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                dismiss()
                (requireActivity() as MainActivity).initializeActionBar()
                (requireActivity() as MainActivity).hideBottomNavbar()
                (requireActivity() as MainActivity).showBottomNavbar()
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

    private fun initializeViewModel() {
        detailsViewModel = ViewModelProvider(viewModelStore, DetailsViewModelFactory())
            .get(DetailsViewModel::class.java)
    }

    private fun setButtonListeners() {
        binding.favouriteToggle.setOnClickListener {
            detailsViewModel.addItemToFirebaseDatabase()
        }
    }

    private fun observeViewModelChanges() {
        detailsViewModel.getDetailedItem(item.id).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                setPosterBasedOnUrl(it, binding.poster)
                binding.title.text = it.title
                binding.icon.setImageResource(getTypeDrawable(it.type))
                binding.year.text = it.year
                binding.seasons.text = adjustSeasonsText(it.totalSeasons)
                binding.genre.text = it.genre
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
        if (item.posterUrl == "N/A") {
            val imageResource: Int = when (item.type) {
                "movie" -> R.mipmap.poster_default_movie
                "series" -> R.mipmap.poster_default_series
                else -> R.mipmap.poster_default_game
            }
            view.setImageResource(imageResource)
        } else Picasso.get().load(item.posterUrl).into(view)
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