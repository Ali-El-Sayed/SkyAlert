package com.example.skyalert.view.screens.bookmark.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyalert.dataSource.local.db.model.BookmarkedWeatherState
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.repository.IWeatherRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookmarkViewModel(private val repo: IWeatherRepo) : ViewModel() {

    private val _bookmarkState: MutableStateFlow<BookmarkedWeatherState> = MutableStateFlow(BookmarkedWeatherState.Loading)
    val bookmarkState = _bookmarkState.asStateFlow()

    suspend fun getBookmarkList() {
        viewModelScope.launch(Dispatchers.IO) {
            _bookmarkState.value = BookmarkedWeatherState.Loading
            repo.getFavoriteWeather().collect {
                _bookmarkState.value = if (it.isEmpty()) BookmarkedWeatherState.Success(mutableListOf())
                else BookmarkedWeatherState.Success(it.toMutableList())
            }
        }
    }

    suspend fun deleteFavoriteWeather(currentWeather: CurrentWeather): Int {
        var result = viewModelScope.async(Dispatchers.IO) { repo.deleteFavoriteWeather(currentWeather) }.await()
        if (result > 0) {
            _bookmarkState.value = BookmarkedWeatherState.Success(
                (_bookmarkState.value as BookmarkedWeatherState.Success).currentWeather.apply {
                    remove(currentWeather)
                }
            )
        }

        return result
    }
}