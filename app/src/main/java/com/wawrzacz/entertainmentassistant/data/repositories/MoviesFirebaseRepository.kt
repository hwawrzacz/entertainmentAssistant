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
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.data.responses.CommonItemsListResponse
import com.wawrzacz.entertainmentassistant.data.responses.DetailedItemResponse

object MoviesFirebaseRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val currentUser = firebaseAuth.currentUser
    private val usersReference: DatabaseReference
    private val itemsReference: DatabaseReference

    private val _movieCreationResult = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val movieCreationResult: LiveData<ResponseStatus> = _movieCreationResult

    init {
        firebaseDatabase.setPersistenceEnabled(true)

        usersReference = firebaseDatabase.getReference(Path.USERS.value)
        itemsReference = firebaseDatabase.getReference(Path.ITEMS.value)
//        itemsReference.keepSynced(false)
    }

    private val _foundAllMovies = MutableLiveData<CommonItemsListResponse>()
    val foundAllMovies: LiveData<CommonItemsListResponse> = _foundAllMovies

    private val _foundToWatchMovies = MutableLiveData<CommonItemsListResponse>()
    val foundToWatchMovies: LiveData<CommonItemsListResponse> = _foundToWatchMovies

    private val _foundWatchedMovies = MutableLiveData<CommonItemsListResponse>()
    val foundWatchedMovies: LiveData<CommonItemsListResponse> = _foundWatchedMovies

    private val _foundFavouritesMovies = MutableLiveData<CommonItemsListResponse>()
    val foundFavouritesMovies: LiveData<CommonItemsListResponse> = _foundFavouritesMovies

    fun getSingleItem(id: String): LiveData<DetailedItemResponse?> {
        val result = MutableLiveData(DetailedItemResponse(null, ResponseStatus.IN_PROGRESS))

        itemsReference.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val movie: DetailedItem? = snapshot.getValue(DetailedItem::class.java)
                if (movie == null)
                    result.value = DetailedItemResponse(null, ResponseStatus.NO_RESULT)
                else
                    result.value = DetailedItemResponse(movie, ResponseStatus.SUCCESS)
            }
            override fun onCancelled(p0: DatabaseError) {
                result.value = DetailedItemResponse(null, ResponseStatus.ERROR)
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

    fun findAllMoviesByTitle(title: String) {
        itemsReference.orderByChild("queryTitle").startAt(title).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("schab", "$snapshot")
                val moviesIds: List<String> = populateItemsIds(snapshot)
                if (moviesIds.isNotEmpty())
                    getMoviesBasedOnIds(moviesIds, _foundAllMovies)
                else {
                    _foundAllMovies.value = CommonItemsListResponse(null, ResponseStatus.NO_RESULT)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                _foundAllMovies.value = CommonItemsListResponse(null, ResponseStatus.ERROR)
            }
        })
    }

    fun getAllItemsBySection(section: WatchableSection) {
        val userId = firebaseAuth.currentUser?.uid
        val targetLiveData: MutableLiveData<CommonItemsListResponse> = when (section) {
            WatchableSection.TO_WATCH-> _foundToWatchMovies
            WatchableSection.WATCHED -> _foundWatchedMovies
            else -> _foundFavouritesMovies
        }

        // Child listener must be added before value listener because otherwise, result can be empty
        usersReference.child("${userId}/${Path.MOVIES.value}/${section.value}")
            .addChildEventListener(object: ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                        val moviesIds: List<String> = populateItemsIds(snapshot)
                        getMoviesBasedOnIds(moviesIds, targetLiveData)
                    }
                    override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
                        val moviesIds: List<String> = populateItemsIds(snapshot)
                        getMoviesBasedOnIds(moviesIds, targetLiveData)
                    }
                    override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {
                        val moviesIds: List<String> = populateItemsIds(snapshot)
                        getMoviesBasedOnIds(moviesIds, targetLiveData)
                    }
                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        val moviesIds: List<String> = populateItemsIds(snapshot)
                        getMoviesBasedOnIds(moviesIds, targetLiveData)
                    }
                    override fun onCancelled(snapshot: DatabaseError) {
//                        targetLiveData.value = null
                    }
                })

        usersReference.child("${userId}/${Path.MOVIES.value}/${section.value}")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val moviesIds: List<String> = populateItemsIds(snapshot)
                    getMoviesBasedOnIds(moviesIds, targetLiveData)
                }
                override fun onCancelled(p0: DatabaseError) {
                    Log.i("schab", "Change cancelled")
                }
            })
    }

    private fun populateItemsIds(snapshot: DataSnapshot): List<String> {
        val moviesIds = mutableListOf<String>()
        for (row in snapshot.children)
            if (row.key != null && row.value != null && row.value != false)
                moviesIds.add(row.key!!)
        return moviesIds
    }

    private fun getMoviesBasedOnIds(moviesIds: List<String>, targetLiveData: MutableLiveData<CommonItemsListResponse>) {
        itemsReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                refreshItemsListFromSnapshot(moviesIds, snapshot, targetLiveData)
            }
            override fun onCancelled(p0: DatabaseError) {
                targetLiveData.value = CommonItemsListResponse(null, ResponseStatus.ERROR)
            }
        })
    }

    private fun refreshItemsListFromSnapshot(
        moviesIds: List<String>,
        dataSnapshot: DataSnapshot,
        targetLiveData: MutableLiveData<CommonItemsListResponse>) {

        val foundMovies = arrayListOf<CommonListItem>()
        for (movieId in moviesIds) {
            val movies = dataSnapshot.children
            for (movieRow in movies) {
                if (movieRow.key == movieId) {
                    val movie = movieRow.getValue(CommonListItem::class.java)
                    if (movie != null) foundMovies.add(movie)
                }
            }
        }
        targetLiveData.value = CommonItemsListResponse(foundMovies, ResponseStatus.SUCCESS)
    }

    fun createItem(item: DetailedItem) {
        _movieCreationResult.value = ResponseStatus.IN_PROGRESS
        val generatedId = itemsReference.push().key

        if (generatedId != null) {
            item.id = generatedId
            itemsReference.child(generatedId).setValue(item).addOnCompleteListener {
                when {
                    it.isSuccessful -> _movieCreationResult.value = ResponseStatus.SUCCESS
                    it.isCanceled -> _movieCreationResult.value = ResponseStatus.ERROR
                    it.isComplete -> _movieCreationResult.value = ResponseStatus.ERROR
                }
                // TODO: Another barbarian solution, which prevent happening an action
                // when newly opened fragment subsribes to result initialized on previous fragment
                _movieCreationResult.value = ResponseStatus.NOT_INITIALIZED
            }
        }
    }

    fun toggleItemSection(section: WatchableSection, movie: DetailedItem?) {
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
        section: WatchableSection?
    ) {
        itemsReference.child(movie.id)
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

    private fun toggleSectionMovieValue(userId: String, movie: DetailedItem, section: WatchableSection?) {
        if (section != null) {
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