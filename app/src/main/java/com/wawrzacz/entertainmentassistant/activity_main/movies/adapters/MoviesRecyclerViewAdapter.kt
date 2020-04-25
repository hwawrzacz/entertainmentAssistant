package com.wawrzacz.entertainmentassistant.activity_main.movies.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.wawrzacz.entertainmentassistant.R
import com.wawrzacz.entertainmentassistant.data.model.MovieSimple

class MoviesRecyclerViewAdapter(var data: List<MovieSimple>): RecyclerView.Adapter<MoviesRecyclerViewAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_recycler_view_element, parent, false) as MaterialCardView
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.title.text = data[position].title
        holder.year.text =  data[position].year
        holder.type.text = capitalizeFirstLetter(data[position].type)
        holder.favourite.setButtonDrawable(getIsFavouriteDrawable(data[position].isFavourite))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MovieViewHolder(viewHolder: MaterialCardView): RecyclerView.ViewHolder(viewHolder) {
        var title = viewHolder.findViewById<TextView>(R.id.movie_title)
        var type = viewHolder.findViewById<TextView>(R.id.movie_category)
        var year = viewHolder.findViewById<TextView>(R.id.movie_year)
        var favourite = viewHolder.findViewById<MaterialCheckBox>(R.id.movie_fav_toggle)
    }

    private fun capitalizeFirstLetter(value: String): String {
        return "${value.substring(0, 1).toUpperCase()}${value.substring(1)}"
    }

    private fun getIsFavouriteDrawable(value: Boolean): Int {
        return when (value) {
            true -> R.drawable.heart_filled_24
            else -> R.drawable.heart_border_24
        }
    }
}