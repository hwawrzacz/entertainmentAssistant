package com.wawrzacz.entertainmentassistant.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.wawrzacz.entertainmentassistant.data.model.MovieSimple

class MovieDiffCallback: DiffUtil.ItemCallback<MovieSimple>() {
    override fun areContentsTheSame(oldItem: MovieSimple, newItem: MovieSimple): Boolean {
        return oldItem.id == newItem.id &&
               oldItem.title == newItem.title &&
               oldItem.year == newItem.year
    }

    override fun areItemsTheSame(oldItem: MovieSimple, newItem: MovieSimple): Boolean {
        return oldItem == newItem
    }
}