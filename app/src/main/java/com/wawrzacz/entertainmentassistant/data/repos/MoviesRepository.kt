package com.wawrzacz.entertainmentassistant.data.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wawrzacz.entertainmentassistant.data.Movie

object MoviesRepository {
    private val _watchedMovies = MutableLiveData<List<Movie>>()
    val watchedMovies: LiveData<List<Movie>> = _watchedMovies

    fun loadWatchedMovies(query: String?) {
        Log.i("schab", "REPO load movies")
        val movieMock = listOf(
            Movie(null, "Pulp fiction", "asd", 179, "Quentin Tarantino", "USA", 1994, true),
            Movie(null, "Fast and Furious", "asd", 125, "Jet Lee", "USA", 1999, true),
            Movie(null, "Enemy", "asd", 142, "Johny Random", "Canada", 2007, false),
            Movie(null, "Frozen", "asd", 117, "Disney", "USA", 2015, false),
            Movie(null, "Rambo", "asd", 124, "Nevermind", "USA", 1997, false),
            Movie(null, "Breaking bad", "asd", 58, "Do not know", "USA", 2010, true)
        )
        var mockListSize = 0
        if (!query.isNullOrBlank())
             mockListSize = (movieMock.size - query.length) % movieMock.size

        _watchedMovies.value = movieMock.subList(0, mockListSize)
    }
}