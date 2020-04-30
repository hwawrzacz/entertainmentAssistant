package com.wawrzacz.entertainmentassistant.data.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem

object MoviesFirebaseRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val usersReference = firebaseDatabase.getReference("users")
    private val moviesReference = firebaseDatabase.getReference("movies")

    object Path {
        const val MOVIES_FAVOURITES = "movies/favourites"
        const val MOVIES_WATCHED = "movies/watched"
        const val MOVIES_TO_WATCH = "movies/to_watch"

        const val SERIES_FAVOURITES = "series/favourites"
        const val SERIES_WATCHED = "series/watched"
        const val SERIES_TO_WATCH = "series/to_watch"

        const val GAMES_FAVOURITES = "games/favourites"
        const val GAMES_PLAYED = "games/played"
        const val GAMES_TO_PLAY = "games/to_play"
    }

    private val _result = MutableLiveData<List<UniversalItem>>()
    val result: LiveData<List<UniversalItem>> = _result

    fun addMovieToCurrentUsersFavourite(movie: DetailedItem?) {
        val currentUserId = firebaseAuth.currentUser?.uid
        when {
            movie == null -> {
                // TODO: Handle null item
                Log.i("schab", "empty movie")
            }
            currentUserId == null -> {
                // TODO: Handle null user
                Log.i("schab", "empty user")
            }
            else -> {
                addMovieToDatabaseAndAssignToUser(currentUserId, movie, Path.MOVIES_FAVOURITES)
            }
        }
    }

    private fun addMovieToDatabaseAndAssignToUser(userId: String,
                                                  movie: DetailedItem,
                                                  userTargetPath: String) {
        moviesReference.child(movie.id)
            .setValue(movie)
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        assignMovieToUser(userId, userTargetPath, movie.id)
                    }
                    it.isComplete -> {
                        Log.i("schab", it.exception?.message.toString())
                    }
                    it.isCanceled -> {
                        Log.i("schab", it.exception?.message.toString())
                    }
                }
            }
    }

    private fun assignMovieToUser(userId: String, path: String, movieId: String) {
        usersReference.child("$userId/$path/$movieId")
            .setValue(true)
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        Log.i("schab", "added successfully")
                    }
                    it.isComplete -> {
                        Log.i("schab", it.exception?.message.toString())
                    }
                    it.isCanceled -> {
                        Log.i("schab", it.exception?.message.toString())
                    }
                }
        }
    }
}