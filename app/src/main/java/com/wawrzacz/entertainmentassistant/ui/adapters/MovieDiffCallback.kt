package com.wawrzacz.entertainmentassistant.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem

class MovieDiffCallback: DiffUtil.ItemCallback<CommonListItem>() {
    override fun areContentsTheSame(oldItem: CommonListItem, newItem: CommonListItem): Boolean {
        return oldItem.id == newItem.id &&
               oldItem.title == newItem.title &&
               oldItem.year == newItem.year
    }

    override fun areItemsTheSame(oldItem: CommonListItem, newItem: CommonListItem): Boolean {
        return oldItem == newItem
    }
}