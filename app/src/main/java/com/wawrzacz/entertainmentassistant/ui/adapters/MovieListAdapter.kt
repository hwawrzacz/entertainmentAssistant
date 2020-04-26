package com.wawrzacz.entertainmentassistant.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.squareup.picasso.Picasso
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.data.model.MovieSimple
import java.util.*

class MovieListAdapter: ListAdapter<MovieSimple, MovieListAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.movie_recycler_view_element, parent, false) as MaterialCardView
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        Picasso.get().load(movie.posterURL).into(holder.poster )
        holder.title.text = movie.title
        holder.year.text =  movie.year
        holder.typeIcon.setImageResource(getTypeDrawable(movie.type))
        holder.favourite.setButtonDrawable(getIsFavouriteDrawable(movie.isFavourite))
    }

    class MovieViewHolder(viewHolder: MaterialCardView): RecyclerView.ViewHolder(viewHolder) {
        var poster: ImageView = viewHolder.findViewById(R.id.movie_poster)
        var title: TextView = viewHolder.findViewById(R.id.movie_title)
        var typeIcon: ImageView = viewHolder.findViewById(R.id.movie_type_icon)
        var year: TextView = viewHolder.findViewById(R.id.movie_year)
        var favourite: MaterialCheckBox = viewHolder.findViewById(R.id.movie_fav_toggle)
    }


    private fun getTypeDrawable(value: String): Int {
        return when (value.toLowerCase(Locale.getDefault())) {
            "movie" -> R.drawable.movies_rounded
            "series" -> R.drawable.series_24
            else -> R.drawable.gamepad
        }
    }

    private fun getIsFavouriteDrawable(value: Boolean): Int {
        return when (value) {
            true -> R.drawable.heart_filled_24
            else -> R.drawable.heart_border_24
        }
    }
}