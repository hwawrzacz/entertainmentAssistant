package com.wawrzacz.entertainmentassistant.data.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.model.Movie
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem

object MoviesFirebaseRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val usersReference = firebaseDatabase.getReference("users")
    private val moviesReference = firebaseDatabase.getReference("movies")

    object Path {
        const val MOVIES = "movies"
        const val SERIES = "series"
        const val GAMES = "games"

        const val WATCHED = "watched"
        const val TO_WATCH = "to_watch"
        const val PLAYED = "played"
        const val TO_PLAY = "to_play"
        const val FAVOURITES = "favourites"
    }

    private val _result = MutableLiveData<List<UniversalItem>>()
    val result: LiveData<List<UniversalItem>> = _result

    private val _foundMovies = MutableLiveData<List<UniversalItem>>()
    val foundMovies: LiveData<List<UniversalItem>> = _foundMovies

    fun getAllItems() {
        val list = mutableListOf<UniversalItem>()

        moviesReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (row in dataSnapshot.children){
                    val item = row.getValue(UniversalItem::class.java)
                    if (item != null) {
                        list.add(item)
                        Log.i("schab", "Data changed: ${item}")
                    }
                }
                _foundMovies.value = list
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.i("schab", "Change cancelled")
            }
        })
    }

    fun addMovieToCurrentUserFavourites(movie: DetailedItem?) {
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
                addMovieToDatabaseAndAssignToUser(currentUserId, movie, Path.MOVIES)
            }
        }
    }

    private fun addMovieToDatabaseAndAssignToUser(
        userId: String,
        movie: DetailedItem,
        userTargetPath: String
    ) {
        moviesReference.child(movie.id)
            .setValue(movie)
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        assignMovieToUser(userId, userTargetPath, movie)
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

    private fun assignMovieToUser(userId: String, path: String, movieId: DetailedItem) {
        usersReference.child("$userId/${Path.MOVIES}/$movieId/${Path.FAVOURITES}").apply {
            child(Path.FAVOURITES).setValue(true)
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
}