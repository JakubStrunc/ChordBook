package cz.jstrunc.chordbook.android.screens.SongsScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jstrunc.chordbook.android.data.api.ApiClient
import cz.jstrunc.chordbook.android.data.api.CategoryResponse
import cz.jstrunc.chordbook.android.data.api.SongListItemResponse
import cz.jstrunc.chordbook.android.data.api.getApiErrorMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * manages the state and API communication for the songs screen
 */
class SongsViewModel : ViewModel() {

    /// current search text
    var searchText by mutableStateOf("")
        private set

    /// songs displayed on the screen
    var songs by mutableStateOf<List<SongListItemResponse>>(emptyList())
        private set

    /// available song categories
    var categories by mutableStateOf<List<CategoryResponse>>(emptyList())
        private set

    /// identifiers of selected category filters
    var selectedCategoryIds by mutableStateOf<Set<String>>(emptySet())
        private set

    /// indicates whether songs are currently loading
    var isSongsLoading by mutableStateOf(false)
        private set

    /// indicates whether categories are currently loading
    var isCategoriesLoading by mutableStateOf(false)
        private set

    /// error related to loading songs
    var songsErrorMessage by mutableStateOf<String?>(null)
        private set

    /// error related to loading categories
    var categoriesErrorMessage by mutableStateOf<String?>(null)
        private set

    /// indicates whether the filter dialog is visible
    var isFilterDialogVisible by mutableStateOf(false)
        private set

    /// connection error displayed on the songs screen
    var connectionErrorMessage by mutableStateOf<String?>(null)
        private set

    /// delayed search operation
    private var searchJob: Job? = null

    init {
        loadSongs()
        loadCategories()
    }

    /**
     * loads songs using the current search text and category filters
     */
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

    /**
     * loads all available categories
     */
    fun loadCategories() {
        viewModelScope.launch {
            isCategoriesLoading = true
            categoriesErrorMessage = null
            connectionErrorMessage = null


            try {
                categories =
                    ApiClient.categoriesApi.getCategoriesRequest()
            } catch (exception: Exception) {
                connectionErrorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isCategoriesLoading = false
            }
        }
    }

    /**
     * updates the search text and reloads songs after a short delay
     */
    fun onSearchTextChange(value: String) {
        searchText = value

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(400)
            loadSongs()
        }
    }

    /**
     * clears the search text and reloads all songs
     */
    fun clearSearch() {
        searchText = ""

        searchJob?.cancel()
        loadSongs()
    }

    /**
     * shows or hides the filter dialog
     */
    fun toggleFilters() {
        isFilterDialogVisible = !isFilterDialogVisible
    }

    /**
     * selects or removes a category filter
     */
    fun toggleCategory(categoryId: String) {
        selectedCategoryIds =
            if (categoryId in selectedCategoryIds) {
                selectedCategoryIds - categoryId
            } else {
                selectedCategoryIds + categoryId
            }

        loadSongs()
    }

    /**
     * retries loading songs and categories
     */
    fun retryLoading() {
        loadSongs()
        loadCategories()
    }

    /**
     * removes all selected category filters
     */
    fun clearFilters() {
        selectedCategoryIds = emptySet()
        loadSongs()
    }
}

