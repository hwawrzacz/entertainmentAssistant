package com.wawrzacz.entertainmentassistant.activity_main.games.sections

import com.wawrzacz.entertainmentassistant.activity_main.games.GamesListFragment
import com.wawrzacz.entertainmentassistant.activity_main.movies.MoviesListFragment
import com.wawrzacz.entertainmentassistant.activity_main.series.SeriesListFragment
import com.wawrzacz.entertainmentassistant.data.enums.PlayableSection
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection

class FavouritesGamesListFragment: GamesListFragment() {
    override var section: PlayableSection = PlayableSection.FAVOURITES
}