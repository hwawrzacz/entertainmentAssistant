package com.wawrzacz.entertainmentassistant.activity_main.series.sections

import com.wawrzacz.entertainmentassistant.activity_main.series.SeriesListFragment
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection

class ToWatchSeriesListFragment: SeriesListFragment() {
    override val section: WatchableSection = WatchableSection.TO_WATCH
}