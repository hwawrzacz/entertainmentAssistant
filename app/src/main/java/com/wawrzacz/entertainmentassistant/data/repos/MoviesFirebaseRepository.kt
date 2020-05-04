package com.wawrzacz.entertainmentassistant.data.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.model.Section

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

    private val _foundToWatchMovies = MutableLiveData<List<CommonListItem>>()
    val foundToWatchMovies: LiveData<List<CommonListItem>> = _foundToWatchMovies

    private val _foundWatchedMovies = MutableLiveData<List<CommonListItem>>()
    val foundWatchedMovies: LiveData<List<CommonListItem>> = _foundWatchedMovies

    private val _foundFavouritesMovies = MutableLiveData<List<CommonListItem>>()
    val foundFavouritesMovies: LiveData<List<CommonListItem>> = _foundFavouritesMovies

    fun getAllMovies(section: Section) {
        val userId = firebaseAuth.currentUser?.uid
        var sectionPath: String
        var targetLiveData: MutableLiveData<List<CommonListItem>>

        Log.i("schab","getting $section movies")

        when (section) {
            Section.TO_WATCH-> {
                sectionPath = Path.TO_WATCH
                targetLiveData = _foundToWatchMovies
            }
            Section.WATCHED -> {
                sectionPath = Path.WATCHED
                targetLiveData = _foundWatchedMovies
            }
            else -> {
                sectionPath = Path.FAVOURITES
                targetLiveData = _foundFavouritesMovies
            }
        }

        usersReference.child("${userId}/${Path.MOVIES}/$sectionPath").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val moviesIds = mutableListOf<String>()
                for (row in dataSnapshot.children)
                    if (row.value != null && row.key != null)
                        moviesIds.add(row.key!!)

                getMoviesBasedOnIds(moviesIds, targetLiveData)
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.i("schab", "Change cancelled")
            }
        })
    }

    private fun getMoviesBasedOnIds(moviesIds: List<String>, targetLiveData: MutableLiveData<List<CommonListItem>>) {
        moviesReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val foundMovies = mutableListOf<CommonListItem>()
                for (movieId in moviesIds) {
                    val movies = dataSnapshot.children
                    for (movieRow in movies) {
                        if (movieRow.key == movieId) {
                            val movie = movieRow.getValue(CommonListItem::class.java)
                            if (movie != null) foundMovies.add(movie)
                        }
                    }
                }
                Log.i("schab", "${foundMovies.size}")
                targetLiveData.value = foundMovies
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    fun addMovieToCurrentUser(section: String, movie: DetailedItem?) {
        Log.i("schab", "addMovieToCurrentUser")
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
                addMovieToDatabaseAndAssignToUser(currentUserId, movie, section)
            }
        }
    }

    private fun addMovieToDatabaseAndAssignToUser(
        userId: String,
        movie: DetailedItem,
        section: String
    ) {
        Log.i("schab", "addMovieToDatabaseAndAssignToUser")
        moviesReference.child(movie.id)
            .setValue(movie)
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        assignMovieToUser(userId, movie, section)
                        Log.i("schab", "Added to general movies")
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

    private fun assignMovieToUser(userId: String, movie: DetailedItem, section: String) {
        val selectedSection = when (section) {
            "watched" -> Path.WATCHED
            "to_watch" -> Path.TO_WATCH
            else -> Path.FAVOURITES
        }

        usersReference.child("$userId/${Path.MOVIES}/$selectedSection/${movie.id}")
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