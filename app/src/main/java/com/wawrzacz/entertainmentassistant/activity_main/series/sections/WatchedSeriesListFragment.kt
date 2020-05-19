package com.wawrzacz.entertainmentassistant.activity_main.series.sections

import com.wawrzacz.entertainmentassistant.activity_main.series.SeriesListFragment
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection

class WatchedSeriesListFragment: SeriesListFragment() {
    override var section: WatchableSection = WatchableSection.WATCHED
}