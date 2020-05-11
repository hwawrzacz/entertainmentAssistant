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
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.data.responses.CommonItemsListResponse
import com.wawrzacz.entertainmentassistant.data.responses.DetailedItemResponse
import java.util.*

object GeneralFirebaseRepository {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val usersReference: DatabaseReference
    private val itemsReference: DatabaseReference

    init {
        firebaseDatabase.setPersistenceEnabled(true)
        usersReference = firebaseDatabase.getReference(Path.USERS.value)
        itemsReference = firebaseDatabase.getReference(Path.ITEMS.value)
        itemsReference.keepSynced(true)
        usersReference.keepSynced(true)
    }

    private val _foundAllMovies = MutableLiveData<CommonItemsListResponse>()
    val foundAllMovies: LiveData<CommonItemsListResponse> = _foundAllMovies

    fun findAllItemsByTitle(title: String) {
        //TODO: This solution is fatal for big database. Use some third-party solution in the future.
        itemsReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<CommonListItem>()
                snapshot.children.forEach {
                    val item = it.getValue(CommonListItem::class.java)
                    if (item != null &&
                        item.title.toLowerCase(Locale.ROOT).contains(title.toLowerCase(Locale.ROOT))
                    )
                        items.add(item)
                }
                if (items.isNullOrEmpty())
                    _foundAllMovies.value = CommonItemsListResponse(null, ResponseStatus.NO_RESULT)
                else {
                    _foundAllMovies.value = CommonItemsListResponse(items, ResponseStatus.SUCCESS)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                _foundAllMovies.value = CommonItemsListResponse(null, ResponseStatus.ERROR)
            }
        })
    }
}