package com.wawrzacz.entertainmentassistant.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.squareup.picasso.Picasso
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.activity_main.browse.BrowseViewModel
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem
import java.util.*

// idDedicated field determines whether or not favourite heart should be displayed. In Browse view it should'n be displayed
class BrowseListAdapter(private val viewModel: BrowseViewModel): ListAdapter<UniversalItem, BrowseListAdapter.UniversalItemViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversalItemViewHolder {
        val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.universal_list_view_element, parent, false) as MaterialCardView

        return UniversalItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: UniversalItemViewHolder, position: Int) {
        val item = getItem(position)
        setPosterBasedOnUrl(item, holder)
        if (item.posterURL == "N/A") {
                val imageResource: Int = when (item.type) {
                    "series" -> R.mipmap.poster_default_series
                    "game" -> R.mipmap.poster_default_game
                    else -> R.mipmap.poster_default_movie
                }
                holder.poster.setImageResource(imageResource)
        } else Picasso.get().load(item.posterURL).into(holder.poster)
        holder.title.text = item.title
        holder.year.text =  item.year
        holder.typeIcon.setImageResource(getTypeDrawable(item.type))
        holder.favourite.visibility = View.GONE
//        (getIsFavouriteDrawable(movie_24.isFavourite))

        holder.itemView.setOnClickListener{
            viewModel.setSelectedItem(item)
        }
    }

    class UniversalItemViewHolder(viewHolder: MaterialCardView): RecyclerView.ViewHolder(viewHolder) {
        var poster: ImageView = viewHolder.findViewById(R.id.movie_poster)
        var title: TextView = viewHolder.findViewById(R.id.movie_title)
        var typeIcon: ImageView = viewHolder.findViewById(R.id.movie_type_icon)
        var year: TextView = viewHolder.findViewById(R.id.movie_year)
        var favourite: MaterialCheckBox = viewHolder.findViewById(R.id.movie_fav_toggle)
    }

    private fun setPosterBasedOnUrl(item: UniversalItem, holder: UniversalItemViewHolder) {
        if (item.posterURL == "N/A") {
            val imageResource: Int = when (item.type) {
                "series" -> R.mipmap.poster_default_series
                "game" -> R.mipmap.poster_default_game
                else -> R.mipmap.poster_default_movie
            }
            holder.poster.setImageResource(imageResource)
        } else Picasso.get().load(item.posterURL).into(holder.poster)
    }

    private fun getTypeDrawable(value: String): Int {
        return when (value.toLowerCase(Locale.getDefault())) {
            "movie" -> R.drawable.movie_24
            "series" -> R.drawable.series_24
            else -> R.drawable.gamepad
        }
    }

//    private fun getIsFavouriteDrawable(value: Boolean): Int {
//        return when (value) {
//            true -> R.drawable.heart_filled_24
//            else -> R.drawable.heart_border_24
//        }
//    }

    private fun openDetailsFragment(id: String) {
        Log.i("schab", "show details for: $id")
    }

    private fun toggleFavourites(id: String) {
        Log.i("schab", "toggle: $id")
    }
}