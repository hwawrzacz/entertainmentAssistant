package com.wawrzacz.entertainmentassistant.activity_main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.repos.MoviesFirebaseRepository
import com.wawrzacz.entertainmentassistant.ui.adapters.TransitViewModel

class MoviesViewModel: ViewModel(), TransitViewModel {
    private val moviesRepository = MoviesFirebaseRepository

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _selectedItem = MutableLiveData<CommonListItem?>()
    override val selectedItem: LiveData<CommonListItem?> = _selectedItem

    val foundToWatchMovies: LiveData<List<CommonListItem>> = Transformations.map(moviesRepository.foundToWatchMovies) {
        _isLoading.value = false
        it
    }
    val foundWatchedMovies: LiveData<List<CommonListItem>> = Transformations.map(moviesRepository.foundWatchedMovies) {
        _isLoading.value = false
        it
    }
    val foundFavouritesMovies: LiveData<List<CommonListItem>> = Transformations.map(moviesRepository.foundFavouritesMovies) {
        _isLoading.value = false
        it
    }

    override fun setSelectedItem(item: CommonListItem?) {
        _selectedItem.value = item
    }

    fun loadData() {
        _isLoading.value = true
    }

    fun findMovies(section: String, query: String) {
        _isLoading.value = true
        moviesRepository.getAllWatchedMovies(section)
    }

}