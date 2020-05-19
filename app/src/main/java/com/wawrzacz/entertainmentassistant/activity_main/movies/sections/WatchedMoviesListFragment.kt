package com.wawrzacz.entertainmentassistant.activity_main.movies.sections

import com.wawrzacz.entertainmentassistant.activity_main.movies.MoviesListFragment
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection

class WatchedMoviesListFragment: MoviesListFragment() {
    override var section: WatchableSection = WatchableSection.WATCHED
}