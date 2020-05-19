package com.wawrzacz.entertainmentassistant.activity_main.series.sections

import com.wawrzacz.entertainmentassistant.activity_main.movies.MoviesListFragment
import com.wawrzacz.entertainmentassistant.activity_main.series.SeriesListFragment
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection

class FavouritesSeriesListFragment: SeriesListFragment() {
    override var section: WatchableSection = WatchableSection.FAVOURITES
}