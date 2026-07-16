package cz.jstrunc.chordbook.android.screens.SongsScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jstrunc.chordbook.android.data.api.ApiClient
import cz.jstrunc.chordbook.android.data.api.CategoryResponse
import cz.jstrunc.chordbook.android.data.api.SongListItemResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import cz.jstrunc.chordbook.android.data.api.getApiErrorMessage
class SongsViewModel : ViewModel() {

    var searchText by mutableStateOf("")
        private set

    var songs by mutableStateOf<List<SongListItemResponse>>(emptyList())
        private set

    var categories by mutableStateOf<List<CategoryResponse>>(emptyList())
        private set

    var selectedCategoryIds by mutableStateOf<Set<String>>(emptySet())
        private set

    var isSongsLoading by mutableStateOf(false)
        private set

    var isCategoriesLoading by mutableStateOf(false)
        private set

    var songsErrorMessage by mutableStateOf<String?>(null)
        private set

    var categoriesErrorMessage by mutableStateOf<String?>(null)
        private set

    var isFilterDialogVisible by mutableStateOf(false)
        private set

    var connectionErrorMessage by mutableStateOf<String?>(null)
        private set

    private var searchJob: Job? = null



    init {
        loadSongs()
        loadCategories()
    }

    fun loadSongs() {
        viewModelScope.launch {
            isSongsLoading = true
            songsErrorMessage = null
            connectionErrorMessage = null

            try {
                songs = ApiClient.songsApi.getSongsRequest(
                    search = searchText
                        .trim()
                        .ifBlank { null },
                    categoryIds = selectedCategoryIds.toList()
                )
            } catch (exception: Exception) {
                connectionErrorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isSongsLoading = false
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            isCategoriesLoading = true
            categoriesErrorMessage = null

            try {
                categories =
                    ApiClient.categoriesApi.getCategoriesRequest()
            } catch (exception: Exception) {
                categoriesErrorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isCategoriesLoading = false
            }
        }
    }

    fun onSearchTextChange(value: String) {
        searchText = value

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(400)
            loadSongs()
        }
    }

    fun clearSearch() {
        searchText = ""

        searchJob?.cancel()
        loadSongs()
    }

    fun toggleFilters() {
        isFilterDialogVisible = !isFilterDialogVisible
    }

    fun toggleCategory(categoryId: String) {
        selectedCategoryIds =
            if (categoryId in selectedCategoryIds) {
                selectedCategoryIds - categoryId
            } else {
                selectedCategoryIds + categoryId
            }

        loadSongs()
    }

    fun retryLoading() {
        loadSongs()
        loadCategories()
    }

    fun clearFilters() {
        selectedCategoryIds = emptySet()
        loadSongs()
    }
}