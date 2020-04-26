package com.wawrzacz.entertainmentassistant.activity_main.movies.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.data.model.MovieSimple
import kotlinx.coroutines.coroutineScope
import java.io.InputStream
import java.net.URL

class MoviesRecyclerViewAdapter(var data: List<MovieSimple>): RecyclerView.Adapter<MoviesRecyclerViewAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_recycler_view_element, parent, false) as MaterialCardView
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

        val movie = data[position]
//        holder.poster.setImageDrawable(getDrawableFromURL(movie.posterURL))
        holder.title.text = data[position].title
        holder.year.text =  data[position].year
        holder.typeIcon.setImageResource(getTypeDrawable(data[position].type))
        holder.favourite.setButtonDrawable(getIsFavouriteDrawable(data[position].isFavourite))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MovieViewHolder(viewHolder: MaterialCardView): RecyclerView.ViewHolder(viewHolder) {
        var poster = viewHolder.findViewById<ImageView>(R.id.movie_poster)
        var title = viewHolder.findViewById<TextView>(R.id.movie_title)
        var typeIcon = viewHolder.findViewById<ImageView>(R.id.movie_type_icon)
        var year = viewHolder.findViewById<TextView>(R.id.movie_year)
        var favourite = viewHolder.findViewById<MaterialCheckBox>(R.id.movie_fav_toggle)
    }

    private fun getDrawableFromURL(stringURL: String): Drawable {
        val imageUrl = URL(stringURL).content as InputStream
        return Drawable.createFromStream(imageUrl,"src")
    }

    private fun capitalizeFirstLetter(value: String): String {
        return "${value.substring(0, 1).toUpperCase()}${value.substring(1)}"
    }

    private fun getTypeDrawable(value: String): Int {
        return when (value.toLowerCase()) {
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