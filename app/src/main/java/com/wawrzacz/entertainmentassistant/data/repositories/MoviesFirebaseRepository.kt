package com.wawrzacz.entertainmentassistant.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wawrzacz.entertainmentassistant.data.enums.Path
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.enums.Section

object MoviesFirebaseRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val usersReference = firebaseDatabase.getReference("users")
    private val moviesReference = firebaseDatabase.getReference(Path.MOVIES.value)

    private val _foundToWatchMovies = MutableLiveData<List<CommonListItem>>()
    val foundToWatchMovies: LiveData<List<CommonListItem>> = _foundToWatchMovies

    private val _foundWatchedMovies = MutableLiveData<List<CommonListItem>>()
    val foundWatchedMovies: LiveData<List<CommonListItem>> = _foundWatchedMovies

    private val _foundFavouritesMovies = MutableLiveData<List<CommonListItem>>()
    val foundFavouritesMovies: LiveData<List<CommonListItem>> = _foundFavouritesMovies

    fun getAllMovies(section: Section) {
        val userId = firebaseAuth.currentUser?.uid
        val targetLiveData: MutableLiveData<List<CommonListItem>> = when (section) {
            Section.TO_WATCH-> _foundToWatchMovies
            Section.WATCHED -> _foundWatchedMovies
            else -> _foundFavouritesMovies
        }

        usersReference.child("${userId}/${Path.MOVIES.value}/${section.value}").addValueEventListener(object: ValueEventListener {
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
                targetLiveData.value = foundMovies
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    fun addMovieToCurrentUser(section: Section, movie: DetailedItem?) {
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
        section: Section
    ) {
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

    private fun assignMovieToUser(userId: String, movie: DetailedItem, section: Section) {
        usersReference.child("$userId/${Path.MOVIES.value}/${section.value}/${movie.id}")
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