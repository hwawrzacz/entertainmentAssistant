package com.wawrzacz.entertainmentassistant.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem

class MovieDiffCallback: DiffUtil.ItemCallback<UniversalItem>() {
    override fun areContentsTheSame(oldItem: UniversalItem, newItem: UniversalItem): Boolean {
        return oldItem.id == newItem.id &&
               oldItem.title == newItem.title &&
               oldItem.year == newItem.year
    }

    override fun areItemsTheSame(oldItem: UniversalItem, newItem: UniversalItem): Boolean {
        return oldItem == newItem
    }
}