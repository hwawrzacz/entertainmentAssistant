package com.wawrzacz.entertainmentassistant.ui.adapters

import androidx.lifecycle.LiveData
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem

interface ItemPassingViewModel {
    val selectedItem: LiveData<CommonListItem?>
    fun setSelectedItem(item: CommonListItem?)
}