package com.wawrzacz.entertainmentassistant.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wawrzacz.entertainmentassistant.data.enums.ItemSource
import com.wawrzacz.entertainmentassistant.data.enums.ItemType
import com.wawrzacz.entertainmentassistant.data.enums.Path
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.enums.PlayableSection
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.data.responses.CommonItemsListResponse
import com.wawrzacz.entertainmentassistant.data.responses.DetailedItemResponse
import java.util.*

object GamesFirebaseRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val currentUser = firebaseAuth.currentUser
    private val usersReference: DatabaseReference
    private val itemsReference: DatabaseReference

    private val _gameEditionStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val gameEditionStatus: LiveData<ResponseStatus> = _gameEditionStatus

    init {
        usersReference = firebaseDatabase.getReference(Path.USERS.value)
        itemsReference = firebaseDatabase.getReference(Path.ITEMS.value)
    }

    private val _foundToPlayGames = MutableLiveData(CommonItemsListResponse(null, ResponseStatus.NOT_INITIALIZED))
    val foundToPlayGames: LiveData<CommonItemsListResponse> = _foundToPlayGames

    private val _foundPlayedGames = MutableLiveData(CommonItemsListResponse(null, ResponseStatus.NOT_INITIALIZED))
    val foundPlayedGames: LiveData<CommonItemsListResponse> = _foundPlayedGames

    private val _foundFavouritesGames = MutableLiveData(CommonItemsListResponse(null, ResponseStatus.NOT_INITIALIZED))
    val foundFavouritesGames: LiveData<CommonItemsListResponse> = _foundFavouritesGames

    fun getSingleItem(id: String): LiveData<DetailedItemResponse?> {
        val result = MutableLiveData(DetailedItemResponse(null, ResponseStatus.IN_PROGRESS))

        itemsReference.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item: DetailedItem? = snapshot.getValue(DetailedItem::class.java)
                if (item == null)
                    result.value = DetailedItemResponse(null, ResponseStatus.NO_RESULT)
                else{
                    item.source = ItemSource.FIREBASE
                    result.value = DetailedItemResponse(item, ResponseStatus.SUCCESS)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                result.value = DetailedItemResponse(null, ResponseStatus.ERROR)
            }
        })
        return result
    }

    fun getGameSectionValue(itemId: String?, section: PlayableSection): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        if (currentUser != null && itemId != null) {
            val path = "${currentUser.uid}/${Path.GAMES.value}/${section.value}/$itemId"

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

    fun findItemsInSection(itemType: ItemType, section: PlayableSection, title: String?) {
        val userId = firebaseAuth.currentUser?.uid
        val targetLiveData: MutableLiveData<CommonItemsListResponse> = getTargetLiveDataBasedOnSection(section)

        val type = when (itemType) {
            ItemType.MOVIE -> Path.MOVIES
            ItemType.SERIES -> Path.SERIES
            ItemType.GAME -> Path.GAMES
        }

        // Child listener must be added before value listener because otherwise, result can be empty
        usersReference.child("${userId}/${type.value}/${section.value}")
            .addChildEventListener(SectionSearchChildEventListener(targetLiveData, title))

        usersReference.child("${userId}/${type.value}/${section.value}")
            .addValueEventListener(SectionSearchValueEventListener(targetLiveData, title))
    }

    private fun getTargetLiveDataBasedOnSection(section: PlayableSection): MutableLiveData<CommonItemsListResponse> {
        return when (section) {
            PlayableSection.TO_PLAY -> _foundToPlayGames
            PlayableSection.PLAYED -> _foundPlayedGames
            PlayableSection.FAVOURITES -> _foundFavouritesGames
        }
    }

    private fun getFoundGamesIds(snapshot: DataSnapshot): List<String> {
        val gamesIds = mutableListOf<String>()
        for (row in snapshot.children)
            if (row.key != null && row.value != null && row.value != false)
                gamesIds.add(row.key!!)
        return gamesIds
    }

    private fun getItemsBasedOnIds(gamesIds: List<String>, targetLiveData: MutableLiveData<CommonItemsListResponse>) {
        itemsReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                refreshItemsListFromSnapshot(gamesIds, snapshot, targetLiveData)
            }
            override fun onCancelled(p0: DatabaseError) {
                targetLiveData.value = CommonItemsListResponse(null, ResponseStatus.ERROR)
            }
        })
    }

    private fun getGamesBasedOnIdsAndTitle(gamesIds: List<String>, title: String, targetLiveData: MutableLiveData<CommonItemsListResponse>) {
        itemsReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                filterByTitleAndRefresh(gamesIds, title, snapshot, targetLiveData)
            }
            override fun onCancelled(p0: DatabaseError) {
                targetLiveData.value = CommonItemsListResponse(null, ResponseStatus.ERROR)
            }
        })
    }

    private fun refreshItemsListFromSnapshot(
        gamesIds: List<String>,
        dataSnapshot: DataSnapshot,
        targetLiveData: MutableLiveData<CommonItemsListResponse>) {

        val foundGames = arrayListOf<CommonListItem>()
        for (gameId in gamesIds) {
            val games = dataSnapshot.children
            for (gameRow in games) {
                if (gameRow.key == gameId) {
                    val game = gameRow.getValue(CommonListItem::class.java)
                    if (game != null)
                        foundGames.add(game)
                }
            }
        }
        if (foundGames.isNullOrEmpty())
            targetLiveData.value = CommonItemsListResponse(null, ResponseStatus.NO_RESULT)
        else
            targetLiveData.value = CommonItemsListResponse(foundGames, ResponseStatus.SUCCESS)
    }

    private fun filterByTitleAndRefresh(
        gamesIds: List<String>,
        title: String,
        dataSnapshot: DataSnapshot,
        targetLiveData: MutableLiveData<CommonItemsListResponse>) {

        val foundGames = arrayListOf<CommonListItem>()
        for (gameId in gamesIds) {
            val games = dataSnapshot.children
            for (gameRow in games) {
                if (gameRow.key == gameId ) {
                    val game = gameRow.getValue(CommonListItem::class.java)
                    if (game != null &&
                        game.title
                            .toLowerCase(Locale.ROOT)
                            .contains(title.toLowerCase(Locale.ROOT)))
                        foundGames.add(game)
                }
            }
        }
        if (foundGames.isNullOrEmpty())
            targetLiveData.value = CommonItemsListResponse(null, ResponseStatus.NO_RESULT)
        else
            targetLiveData.value = CommonItemsListResponse(foundGames, ResponseStatus.SUCCESS)
    }

    fun createItem(item: DetailedItem) {
        _gameEditionStatus.value = ResponseStatus.IN_PROGRESS
        val generatedId = itemsReference.push().key

        if (generatedId != null) {
            item.id = generatedId
            itemsReference.child(generatedId).setValue(item).addOnCompleteListener {
                when {
                    it.isSuccessful -> _gameEditionStatus.value = ResponseStatus.SUCCESS
                    it.isCanceled -> _gameEditionStatus.value = ResponseStatus.ERROR
                    it.isComplete -> _gameEditionStatus.value = ResponseStatus.ERROR
                }
                // TODO: Another barbarian solution, which prevent happening an action
                // when newly opened fragment subscribes to result initialized on previous fragment
                _gameEditionStatus.value = ResponseStatus.NOT_INITIALIZED
            }
        }
    }

    fun updateItem(item: DetailedItem) {
        itemsReference.child(item.id).setValue(item).addOnCompleteListener {
            when {
                it.isSuccessful -> _gameEditionStatus.value = ResponseStatus.SUCCESS
                it.isCanceled -> _gameEditionStatus.value = ResponseStatus.ERROR
                it.isComplete -> _gameEditionStatus.value = ResponseStatus.ERROR
            }
            _gameEditionStatus.value = ResponseStatus.NOT_INITIALIZED
        }
    }

    fun toggleItemSection(section: PlayableSection, game: DetailedItem?) {
        val currentUserId = firebaseAuth.currentUser?.uid
        when {
            // TODO: Handle null item
            game == null -> Log.i("schab", "empty game")
            // TODO: Handle null user
            currentUserId == null -> Log.i("schab", "empty user")
            else -> addItemToDatabaseAndAssignToUser(currentUserId, game, section)
        }
    }

    private fun addItemToDatabaseAndAssignToUser(
        userId: String,
        item: DetailedItem,
        section: PlayableSection?
    ) {
        itemsReference.child(item.id)
            .setValue(item)
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        toggleSectionItemValue(userId, item, section)
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

    private fun toggleSectionItemValue(userId: String, item: DetailedItem, section: PlayableSection?) {
        if (section != null) {
            val type = when (item.type) {
                ItemType.MOVIE -> Path.MOVIES
                ItemType.SERIES -> Path.SERIES
                ItemType.GAME -> Path.GAMES
            }

            val path = "$userId/${type.value}/${section.value}/${item.id}"
            usersReference.child(path)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var newValue = snapshot.getValue(Boolean::class.java)
                        if (newValue === null) newValue = false
                        newValue = !newValue

                        setNewGameSectionValue(path, newValue)
                    }
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
        }
    }

    private fun setNewGameSectionValue(path: String, value: Boolean) {
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
            val gamesIds: List<String> = getFoundGamesIds(snapshot)
            if (title.isNullOrBlank())
                getItemsBasedOnIds(gamesIds, targetLiveData)
            else
                getGamesBasedOnIdsAndTitle(gamesIds, title, targetLiveData)
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
            val gamesIds: List<String> = getFoundGamesIds(snapshot)
            if (title.isNullOrBlank())
                getItemsBasedOnIds(gamesIds, targetLiveData)
            else
                getGamesBasedOnIdsAndTitle(gamesIds, title, targetLiveData)
        }
        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
            val gamesIds: List<String> = getFoundGamesIds(snapshot)
            if (title.isNullOrBlank())
                getItemsBasedOnIds(gamesIds, targetLiveData)
            else
                getGamesBasedOnIdsAndTitle(gamesIds, title, targetLiveData)
        }
        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {
            val gamesIds: List<String> = getFoundGamesIds(snapshot)
            if (title.isNullOrBlank())
                getItemsBasedOnIds(gamesIds, targetLiveData)
            else
                getGamesBasedOnIdsAndTitle(gamesIds, title, targetLiveData)
        }
        override fun onChildRemoved(snapshot: DataSnapshot) {
            val gamesIds: List<String> = getFoundGamesIds(snapshot)
            if (title.isNullOrBlank())
                getItemsBasedOnIds(gamesIds, targetLiveData)
            else
                getGamesBasedOnIdsAndTitle(gamesIds, title, targetLiveData)
        }
        override fun onCancelled(snapshot: DatabaseError) {
            targetLiveData.value = CommonItemsListResponse(null, ResponseStatus.ERROR)
        }
    }
}