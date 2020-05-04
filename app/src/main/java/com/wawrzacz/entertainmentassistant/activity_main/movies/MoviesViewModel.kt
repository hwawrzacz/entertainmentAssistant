package com.wawrzacz.entertainmentassistant.activity_main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.model.SearchResult
import com.wawrzacz.entertainmentassistant.data.repos.MoviesFirebaseRepository
import com.wawrzacz.entertainmentassistant.ui.adapters.TransitViewModel

class MoviesViewModel: ViewModel(), TransitViewModel {
    private val moviesRepository = MoviesFirebaseRepository


    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hasAnyResult = MutableLiveData<SearchResult>(SearchResult.NOT_INITIALIZED)
    val hasAnyResult: LiveData<SearchResult> = _hasAnyResult

    private val _selectedItem = MutableLiveData<CommonListItem?>()
    override val selectedItem: LiveData<CommonListItem?> = _selectedItem

    val foundToWatchMovies: LiveData<List<CommonListItem>> = Transformations.map(moviesRepository.foundToWatchMovies) {
        _isLoading.value = false
        determineResultState(it)
        it
    }
    val foundWatchedMovies: LiveData<List<CommonListItem>> = Transformations.map(moviesRepository.foundWatchedMovies) {
        _isLoading.value = false
        determineResultState(it)
        it
    }
    val foundFavouritesMovies: LiveData<List<CommonListItem>> = Transformations.map(moviesRepository.foundFavouritesMovies) {
        _isLoading.value = false
        determineResultState(it)
        it
    }

    override fun setSelectedItem(item: CommonListItem?) {
        _selectedItem.value = item
    }

    fun findMovies(section: String, query: String) {
        _isLoading.value = true
        moviesRepository.getAllMovies(section)
    }

    private fun determineResultState(liveDataValue: List<CommonListItem>?) {
        _hasAnyResult.value = when {
            liveDataValue == null -> SearchResult.ERROR_GETTING_DATA
            liveDataValue.isEmpty() -> SearchResult.NO_RESULTS
            else -> SearchResult.SUCCESS
        }
    }

}