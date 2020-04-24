package com.wawrzacz.entertainmentassistant.activity_main.movies.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.data.Movie

class MoviesRecyclerViewAdapter(var data: List<Movie>): RecyclerView.Adapter<MoviesRecyclerViewAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_recycler_view_element, parent, false) as MaterialCardView
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.title.text = data[position].title
        holder.country.text =  data[position].country
        holder.year.text =  data[position].year.toString()
        holder.duration.text =  data[position].duration.toString()
        holder.director.text =  data[position].director
        holder.favourite.setButtonDrawable(getIsFavouriteDrawable(data[position].isFavourite))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MovieViewHolder(viewHolder: MaterialCardView): RecyclerView.ViewHolder(viewHolder) {
        var title = viewHolder.findViewById<TextView>(R.id.movie_title)
        var country = viewHolder.findViewById<TextView>(R.id.movie_country)
        var year = viewHolder.findViewById<TextView>(R.id.movie_year)
        var duration = viewHolder.findViewById<TextView>(R.id.movie_duration)
        var director = viewHolder.findViewById<TextView>(R.id.movie_director)
        var favourite = viewHolder.findViewById<MaterialCheckBox>(R.id.movie_fav_toggle)
    }

    private fun getIsFavouriteDrawable(value: Boolean): Int {
        return when (value) {
            true -> R.drawable.heart_filled_24
            else -> R.drawable.heart_border_24
        }
    }
}