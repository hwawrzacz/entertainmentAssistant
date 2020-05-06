package com.wawrzacz.entertainmentassistant.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wawrzacz.entertainmentassistant.data.enums.Path
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection

object MoviesFirebaseRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val currentUser = firebaseAuth.currentUser
    private val usersReference: DatabaseReference
    private val moviesReference: DatabaseReference

    init {
        firebaseDatabase.setPersistenceEnabled(true)

        usersReference = firebaseDatabase.getReference(Path.USERS.value)
        moviesReference = firebaseDatabase.getReference(Path.MOVIES.value)
        moviesReference.keepSynced(true)
    }

    private val _foundToWatchMovies = MutableLiveData<List<CommonListItem>?>()
    val foundToWatchMovies: LiveData<List<CommonListItem>?> = _foundToWatchMovies

    private val _foundWatchedMovies = MutableLiveData<List<CommonListItem>?>()
    val foundWatchedMovies: LiveData<List<CommonListItem>?> = _foundWatchedMovies

    private val _foundFavouritesMovies = MutableLiveData<List<CommonListItem>?>()
    val foundFavouritesMovies: LiveData<List<CommonListItem>?> = _foundFavouritesMovies

    fun getSingleMovie(id: String): LiveData<DetailedItem?> {
        val result = MutableLiveData<DetailedItem?>()

        moviesReference.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val movie: DetailedItem? = snapshot.getValue(DetailedItem::class.java)
                result.value = movie
            }
            override fun onCancelled(p0: DatabaseError) {
                result.value = null
            }
        })
        return result
    }

    fun getMovieSectionValue(movieId: String?, section: WatchableSection): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        if (currentUser != null && movieId != null) {
            val path = "${currentUser.uid}/${Path.MOVIES.value}/${section.value}/$movieId"

            usersReference.child(path).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Boolean::class.java)
                    result.value = value
                }
                override fun onCancelled(snapshot: DatabaseError) {
                    result.value = null
                }
            })
        }

        return result
    }

    fun getAllMovies(section: WatchableSection) {
        val userId = firebaseAuth.currentUser?.uid
        val targetLiveData: MutableLiveData<List<CommonListItem>?> = when (section) {
            WatchableSection.TO_WATCH-> _foundToWatchMovies
            WatchableSection.WATCHED -> _foundWatchedMovies
            else -> _foundFavouritesMovies
        }

        // Child listener must be added before value listener because otherwise, result can be empty
        usersReference.child("${userId}/${Path.MOVIES.value}/${section.value}")
            .addChildEventListener(object: ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                        val moviesIds: List<String> = populateMoviesIds(snapshot)
                        getMoviesBasedOnIds(moviesIds, targetLiveData)
                    }
                    override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
                        Log.i("schab", "child changed $snapshot")
                        val moviesIds: List<String> = populateMoviesIds(snapshot)
                        getMoviesBasedOnIds(moviesIds, targetLiveData)
                    }
                    override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {
                        val moviesIds: List<String> = populateMoviesIds(snapshot)
                        getMoviesBasedOnIds(moviesIds, targetLiveData)
                    }
                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        val moviesIds: List<String> = populateMoviesIds(snapshot)
                        getMoviesBasedOnIds(moviesIds, targetLiveData)
                    }
                    override fun onCancelled(snapshot: DatabaseError) {
//                        targetLiveData.value = null
                    }
                })

        usersReference.child("${userId}/${Path.MOVIES.value}/${section.value}")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val moviesIds: List<String> = populateMoviesIds(snapshot)
                    getMoviesBasedOnIds(moviesIds, targetLiveData)
                }
                override fun onCancelled(p0: DatabaseError) {
                    Log.i("schab", "Change cancelled")
                }
            })
    }

    private fun populateMoviesIds(snapshot: DataSnapshot): List<String> {
        val moviesIds = mutableListOf<String>()
        for (row in snapshot.children)
            if (row.key != null && row.value != null && row.value != false)
                moviesIds.add(row.key!!)
        return moviesIds
    }

    private fun getMoviesBasedOnIds(moviesIds: List<String>, targetLiveData: MutableLiveData<List<CommonListItem>?>) {
        moviesReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                refreshMoviesListFromSnapshot(moviesIds, snapshot, targetLiveData)
            }
            override fun onCancelled(p0: DatabaseError) {
//                targetLiveData.value = null
            }
        })
    }

    private fun refreshMoviesListFromSnapshot(
        moviesIds: List<String>,
        dataSnapshot: DataSnapshot,
        targetLiveData: MutableLiveData<List<CommonListItem>?>) {

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
        targetLiveData.value = foundMovies}

    fun toggleMovieSection(section: WatchableSection, movie: DetailedItem?) {
        val currentUserId = firebaseAuth.currentUser?.uid
        when {
            // TODO: Handle null item
            movie == null -> Log.i("schab", "empty movie")
            // TODO: Handle null user
            currentUserId == null -> Log.i("schab", "empty user")
            else -> addMovieToDatabaseAndAssignToUser(currentUserId, movie, section)
        }
    }

    private fun addMovieToDatabaseAndAssignToUser(
        userId: String,
        movie: DetailedItem,
        section: WatchableSection
    ) {
        moviesReference.child(movie.id)
            .setValue(movie)
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        toggleSectionMovieValue(userId, movie, section)
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

    private fun toggleSectionMovieValue(userId: String, movie: DetailedItem, section: WatchableSection) {
        val path = "$userId/${Path.MOVIES.value}/${section.value}/${movie.id}"
        usersReference.child(path)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var newValue = snapshot.getValue(Boolean::class.java)
                    if (newValue === null) newValue = false
                    newValue = !newValue

                    setNewMovieSectionValue(path, newValue)
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
    }

    private fun setNewMovieSectionValue(path: String, value: Boolean) {
        usersReference.child(path)
            .setValue(value)
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