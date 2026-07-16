package cz.jstrunc.chordbook.android.screens.songdetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jstrunc.chordbook.android.data.api.ApiClient
import cz.jstrunc.chordbook.android.data.api.CategoryResponse
import cz.jstrunc.chordbook.android.data.api.CreateCategoryRequest
import cz.jstrunc.chordbook.android.data.api.SongDetailResponse
import cz.jstrunc.chordbook.android.data.api.getApiErrorMessage
import kotlinx.coroutines.launch

/**
 * manages the state and API communication for the song detail screen
 */
class SongDetailViewModel : ViewModel() {

    /// currently displayed song
    var song by mutableStateOf<SongDetailResponse?>(null)
        private set

    /// indicates whether the song is loading
    var isLoading by mutableStateOf(false)
        private set

    /// indicates whether the song is being deleted
    var isDeleting by mutableStateOf(false)
        private set

    /// error related to song operations
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /// categories available for assignment
    var availableCategories by mutableStateOf<List<CategoryResponse>>(emptyList())
        private set

    /// indicates whether categories are loading
    var isCategoriesLoading by mutableStateOf(false)
        private set

    /// error related to category operations
    var categoriesErrorMessage by mutableStateOf<String?>(null)
        private set

    /// indicates whether a category operation is in progress
    var isCategoryUpdating by mutableStateOf(false)
        private set

    /// network connection error
    var connectionErrorMessage by mutableStateOf<String?>(null)
        private set

    /**
     * loads the selected song
     */
    fun loadSong(songId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            connectionErrorMessage = null

            try {
                song = ApiClient.songsApi
                    .getSongDetailRequest(songId)
            } catch (exception: Exception) {
                song = null
                connectionErrorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isLoading = false
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
                availableCategories =
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
     * assigns an existing category to the song
     */
    fun addCategory(
        songId: String,
        categoryId: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isCategoryUpdating = true
            categoriesErrorMessage = null
            connectionErrorMessage = null


            try {
                val response =
                    ApiClient.songsApi.addSongCategoryRequest(
                        songId = songId,
                        categoryId = categoryId
                    )

                when {
                    response.isSuccessful -> {
                        loadSong(songId)
                        onSuccess()
                    }

                    response.code() == 409 -> {
                        categoriesErrorMessage =
                            "Kategorie už je k písničce přiřazena."
                    }

                    response.code() == 404 -> {
                        categoriesErrorMessage =
                            "Písnička nebo kategorie nebyla nalezena."
                    }

                    else -> {
                        categoriesErrorMessage =
                            "Kategorie se nepodařilo přidat. Kód: ${response.code()}"
                    }
                }
            } catch (exception: Exception) {
                connectionErrorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isCategoryUpdating = false
            }
        }
    }

    /**
     * removes a category from the song
     */
    fun deleteCategory(
        songId: String,
        categoryId: String
    ) {
        viewModelScope.launch {
            isCategoryUpdating = true
            categoriesErrorMessage = null
            connectionErrorMessage = null


            try {
                val response =
                    ApiClient.songsApi.deleteSongCategoryRequest(
                        songId = songId,
                        categoryId = categoryId
                    )

                when {
                    response.isSuccessful -> {
                        loadSong(songId)
                    }

                    response.code() == 404 -> {
                        categoriesErrorMessage =
                            "Kategorie už u písničky není."
                    }

                    else -> {
                        categoriesErrorMessage =
                            "Kategorie se nepodařilo odebrat. Kód: ${response.code()}"
                    }
                }
            } catch (exception: Exception) {
                connectionErrorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isCategoryUpdating = false
            }
        }
    }

    /**
     * creates a new category and assigns it to the song
     */
    fun createCategory(
        songId: String,
        categoryName: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isCategoryUpdating = true
            categoriesErrorMessage = null
            connectionErrorMessage = null


            try {
                val trimmedName = categoryName.trim()

                if (trimmedName.isBlank()) {
                    categoriesErrorMessage =
                        "Název kategorie nesmí být prázdný."
                    return@launch
                }

                val createdCategory =
                    ApiClient.categoriesApi.createCategoryRequest(
                        CreateCategoryRequest(
                            name = trimmedName
                        )
                    )

                val addResponse =
                    ApiClient.songsApi.addSongCategoryRequest(
                        songId = songId,
                        categoryId = createdCategory.id
                    )

                when {
                    addResponse.isSuccessful -> {
                        loadCategories()
                        loadSong(songId)
                        onSuccess()
                    }

                    addResponse.code() == 404 -> {
                        categoriesErrorMessage =
                            "Kategorie byla vytvořena, ale písnička nebyla nalezena."
                    }

                    addResponse.code() == 409 -> {
                        categoriesErrorMessage =
                            "Kategorie už je k písničce přiřazena."
                    }

                    else -> {
                        categoriesErrorMessage =
                            "Kategorie byla vytvořena, ale nepodařilo se ji přiřadit. Kód: ${addResponse.code()}"
                    }
                }
            } catch (exception: Exception) {
                connectionErrorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isCategoryUpdating = false
            }
        }
    }

    /**
     * deletes the selected song
     */
    fun deleteSong(
        songId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isDeleting = true
            errorMessage = null
            connectionErrorMessage = null


            try {
                val response =
                    ApiClient.songsApi.deleteSongRequest(songId)

                when {
                    response.isSuccessful -> {
                        song = null
                        onSuccess()
                    }

                    response.code() == 404 -> {
                        errorMessage =
                            "Písnička už neexistuje."
                    }

                    else -> {
                        errorMessage =
                            "Písničku se nepodařilo smazat. Kód: ${response.code()}"
                    }
                }
            } catch (exception: Exception) {
                connectionErrorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isDeleting = false
            }
        }
    }
}