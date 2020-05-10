package com.wawrzacz.entertainmentassistant.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wawrzacz.entertainmentassistant.data.enums.ItemSource
import com.wawrzacz.entertainmentassistant.data.enums.Path
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.data.responses.CommonItemsListResponse
import com.wawrzacz.entertainmentassistant.data.responses.DetailedItemResponse
import java.util.*

object MoviesFirebaseRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val currentUser = firebaseAuth.currentUser
    private val usersReference: DatabaseReference
    private val itemsReference: DatabaseReference

    private val _movieEditionStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val movieEditionStatus: LiveData<ResponseStatus> = _movieEditionStatus

    init {
        firebaseDatabase.setPersistenceEnabled(true)

        usersReference = firebaseDatabase.getReference(Path.USERS.value)
        itemsReference = firebaseDatabase.getReference(Path.ITEMS.value)
//        itemsReference.keepSynced(false)
    }

    private val _foundAllItems = MutableLiveData<CommonItemsListResponse>()
    val foundAllItems: LiveData<CommonItemsListResponse> = _foundAllItems

    private val _foundToWatchMovies = MutableLiveData(CommonItemsListResponse(null, ResponseStatus.NOT_INITIALIZED))
    val foundToWatchMovies: LiveData<CommonItemsListResponse> = _foundToWatchMovies

    private val _foundWatchedMovies = MutableLiveData(CommonItemsListResponse(null, ResponseStatus.NOT_INITIALIZED))
    val foundWatchedMovies: LiveData<CommonItemsListResponse> = _foundWatchedMovies

    private val _foundFavouritesMovies = MutableLiveData(CommonItemsListResponse(null, ResponseStatus.NOT_INITIALIZED))
    val foundFavouritesMovies: LiveData<CommonItemsListResponse> = _foundFavouritesMovies

    fun getSingleItem(id: String): LiveData<DetailedItemResponse?> {
        val result = MutableLiveData(DetailedItemResponse(null, ResponseStatus.IN_PROGRESS))

        itemsReference.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val movie: DetailedItem? = snapshot.getValue(DetailedItem::class.java)
                if (movie == null)
                    result.value = DetailedItemResponse(null, ResponseStatus.NO_RESULT)
                else{
                    movie.source = ItemSource.FIREBASE
                    result.value = DetailedItemResponse(movie, ResponseStatus.SUCCESS)
                }
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

    fun findAllItemsByTitle(title: String) {
        //TODO: This solution is fatal for big database. Use some third-party solution in the future.
        itemsReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<CommonListItem>()
                snapshot.children.forEach {
                    val item = it.getValue(CommonListItem::class.java)
                    if (item != null && item.title
                            .toLowerCase(Locale.ROOT)
                            .contains(title.toLowerCase()))
                        items.add(item)
                }
                if (items.isNullOrEmpty())
                    _foundAllItems.value = CommonItemsListResponse(null, ResponseStatus.NO_RESULT)
                else {
                    Log.i("schabFIREBASE", "Found ${items.size} items")
                    _foundAllItems.value = CommonItemsListResponse(items, ResponseStatus.SUCCESS)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                _foundAllItems.value = CommonItemsListResponse(null, ResponseStatus.ERROR)
            }
        })
    }

    fun findItemsInSection(section: WatchableSection, title: String?) {
        val userId = firebaseAuth.currentUser?.uid
        val targetLiveData: MutableLiveData<CommonItemsListResponse> = getTargetLiveDataBasedOnSection(section)

        // Child listener must be added before value listener because otherwise, result can be empty
        usersReference.child("${userId}/${Path.MOVIES.value}/${section.value}")
            .addChildEventListener(SectionSearchChildEventListener(targetLiveData, title))

        usersReference.child("${userId}/${Path.MOVIES.value}/${section.value}")
            .addValueEventListener(SectionSearchValueEventListener(targetLiveData, title))
    }

    private fun getTargetLiveDataBasedOnSection(section: WatchableSection): MutableLiveData<CommonItemsListResponse> {
        return when (section) {
            WatchableSection.TO_WATCH -> _foundToWatchMovies
            WatchableSection.WATCHED -> _foundWatchedMovies
            else -> _foundFavouritesMovies
        }
    }

    private fun getFoundItemsIds(snapshot: DataSnapshot): List<String> {
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

    private fun getMoviesBasedOnIdsAndTitle(moviesIds: List<String>, title: String, targetLiveData: MutableLiveData<CommonItemsListResponse>) {
        itemsReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                filterByTitleAndRefresh(moviesIds, title, snapshot, targetLiveData)
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
                    if (movie != null)
                        foundMovies.add(movie)
                }
            }
        }
        if (foundMovies.isNullOrEmpty())
            targetLiveData.value = CommonItemsListResponse(null, ResponseStatus.NO_RESULT)
        else
            targetLiveData.value = CommonItemsListResponse(foundMovies, ResponseStatus.SUCCESS)
    }

    private fun filterByTitleAndRefresh(
        moviesIds: List<String>,
        title: String,
        dataSnapshot: DataSnapshot,
        targetLiveData: MutableLiveData<CommonItemsListResponse>) {

        val foundMovies = arrayListOf<CommonListItem>()
        for (movieId in moviesIds) {
            val movies = dataSnapshot.children
            for (movieRow in movies) {
                if (movieRow.key == movieId ) {
                    val movie = movieRow.getValue(CommonListItem::class.java)
                    if (movie != null &&
                        movie.title
                            .toLowerCase(Locale.ROOT)
                            .contains(title.toLowerCase(Locale.ROOT)))
                        foundMovies.add(movie)
                }
            }
        }
        if (foundMovies.isNullOrEmpty())
            targetLiveData.value = CommonItemsListResponse(null, ResponseStatus.NO_RESULT)
        else
            targetLiveData.value = CommonItemsListResponse(foundMovies, ResponseStatus.SUCCESS)
    }

    fun createItem(item: DetailedItem) {
        _movieEditionStatus.value = ResponseStatus.IN_PROGRESS
        val generatedId = itemsReference.push().key

        if (generatedId != null) {
            item.id = generatedId
            itemsReference.child(generatedId).setValue(item).addOnCompleteListener {
                when {
                    it.isSuccessful -> _movieEditionStatus.value = ResponseStatus.SUCCESS
                    it.isCanceled -> _movieEditionStatus.value = ResponseStatus.ERROR
                    it.isComplete -> _movieEditionStatus.value = ResponseStatus.ERROR
                }
                // TODO: Another barbarian solution, which prevent happening an action
                // when newly opened fragment subsribes to result initialized on previous fragment
                _movieEditionStatus.value = ResponseStatus.NOT_INITIALIZED
            }
        }
    }

    fun updateItem(item: DetailedItem) {
        itemsReference.child(item.id).setValue(item).addOnCompleteListener {
            when {
                it.isSuccessful -> _movieEditionStatus.value = ResponseStatus.SUCCESS
                it.isCanceled -> _movieEditionStatus.value = ResponseStatus.ERROR
                it.isComplete -> _movieEditionStatus.value = ResponseStatus.ERROR
            }
            _movieEditionStatus.value = ResponseStatus.NOT_INITIALIZED
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

    private class SectionSearchValueEventListener(
        val targetLiveData: MutableLiveData<CommonItemsListResponse>,
        val title: String?
    ): ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val moviesIds: List<String> = getFoundItemsIds(snapshot)
            if (title.isNullOrBlank())
                getMoviesBasedOnIds(moviesIds, targetLiveData)
            else
                getMoviesBasedOnIdsAndTitle(moviesIds, title, targetLiveData)
        }
        override fun onCancelled(p0: DatabaseError) {
            targetLiveData.value = CommonItemsListResponse(null, ResponseStatus.ERROR)
        }
    }

    private class SectionSearchChildEventListener(
        val targetLiveData: MutableLiveData<CommonItemsListResponse>,
        val title: String?
    ): ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val moviesIds: List<String> = getFoundItemsIds(snapshot)
            if (title.isNullOrBlank())
                getMoviesBasedOnIds(moviesIds, targetLiveData)
            else
                getMoviesBasedOnIdsAndTitle(moviesIds, title, targetLiveData)
        }
        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
            val moviesIds: List<String> = getFoundItemsIds(snapshot)
            if (title.isNullOrBlank())
                getMoviesBasedOnIds(moviesIds, targetLiveData)
            else
                getMoviesBasedOnIdsAndTitle(moviesIds, title, targetLiveData)
        }
        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {
            val moviesIds: List<String> = getFoundItemsIds(snapshot)
            if (title.isNullOrBlank())
                getMoviesBasedOnIds(moviesIds, targetLiveData)
            else
                getMoviesBasedOnIdsAndTitle(moviesIds, title, targetLiveData)
        }
        override fun onChildRemoved(snapshot: DataSnapshot) {
            val moviesIds: List<String> = getFoundItemsIds(snapshot)
            if (title.isNullOrBlank())
                getMoviesBasedOnIds(moviesIds, targetLiveData)
            else
                getMoviesBasedOnIdsAndTitle(moviesIds, title, targetLiveData)
        }
        override fun onCancelled(snapshot: DatabaseError) {
            targetLiveData.value = CommonItemsListResponse(null, ResponseStatus.ERROR)
        }
    }
}