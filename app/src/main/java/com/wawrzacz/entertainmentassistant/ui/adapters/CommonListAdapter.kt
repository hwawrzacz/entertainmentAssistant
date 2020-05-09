package com.wawrzacz.entertainmentassistant.ui.adapters

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
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import java.util.*

// idDedicated field determines whether or not favourite heart should be displayed. In Browse view it should'n be displayed
class CommonListAdapter(private val browseViewModel: TransitViewModel):
    ListAdapter<CommonListItem, CommonListAdapter.CommonListItemViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonListItemViewHolder {
        val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.universal_list_view_element, parent, false) as MaterialCardView

        return CommonListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommonListItemViewHolder, position: Int) {
        val item = getItem(position)
        setPosterBasedOnUrl(item, holder)
        holder.title.text = item.title
        holder.year.text =  item.year
        holder.typeIcon.setImageResource(getTypeDrawable(item.type))
        holder.favourite.visibility = View.GONE

        holder.itemView.setOnClickListener{
            browseViewModel.setSelectedItem(item)
            browseViewModel.setSelectedItem(null)
            /*  TODO: Improve above
                Yep, that's kind of barbarian solution, but let me explain.
                When the item is set, observer from BrowseFragment automatically reacts for change
                and shows details fragment. It happens every time the BrowseFragment fragment is reopened.
                To avoid this, item is immediately being nulled, and that is handled in BrowseFragment:
                When null value comes from viewModel, then details fragment won't be shown

                I believe, there's much better solution, and I'll fix it, but not today.
             */
        }
    }

    private fun setPosterBasedOnUrl(item: CommonListItem, holder: CommonListItemViewHolder) {
        if (item.posterUrl == "N/A" || item.posterUrl.isNullOrBlank()) {
            val imageResource: Int = when (item.type) {
                "series" -> R.mipmap.poster_default_series
                "game" -> R.mipmap.poster_default_game
                else -> R.mipmap.poster_default_movie
            }
            holder.poster.setImageResource(imageResource)
        } else Picasso.get().load(item.posterUrl).into(holder.poster)
    }

    private fun getTypeDrawable(value: String): Int {
        return when (value.toLowerCase(Locale.getDefault())) {
            "movie" -> R.drawable.movie_24
            "series" -> R.drawable.series_24
            else -> R.drawable.gamepad_filled
        }
    }

    class CommonListItemViewHolder(viewHolder: MaterialCardView): RecyclerView.ViewHolder(viewHolder) {
        var poster: ImageView = viewHolder.findViewById(R.id.movie_poster)
        var title: TextView = viewHolder.findViewById(R.id.movie_title)
        var typeIcon: ImageView = viewHolder.findViewById(R.id.movie_type_icon)
        var year: TextView = viewHolder.findViewById(R.id.movie_year)
        var favourite: MaterialCheckBox = viewHolder.findViewById(R.id.movie_fav_toggle)
    }
}