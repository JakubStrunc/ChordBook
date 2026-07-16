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

class SongDetailViewModel : ViewModel() {

    var song by mutableStateOf<SongDetailResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isDeleting by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var availableCategories by mutableStateOf<List<CategoryResponse>>(emptyList())
        private set

    var isCategoriesLoading by mutableStateOf(false)
        private set

    var categoriesErrorMessage by mutableStateOf<String?>(null)
        private set

    var isCategoryUpdating by mutableStateOf(false)
        private set

    var connectionErrorMessage by mutableStateOf<String?>(null)
        private set

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

    fun loadCategories() {
        viewModelScope.launch {
            isCategoriesLoading = true
            categoriesErrorMessage = null

            try {
                availableCategories =
                    ApiClient.categoriesApi.getCategoriesRequest()
            } catch (exception: Exception) {
                categoriesErrorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isCategoriesLoading = false
            }
        }
    }

    fun addCategory(
        songId: String,
        categoryId: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isCategoryUpdating = true
            categoriesErrorMessage = null

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
                categoriesErrorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isCategoryUpdating = false
            }
        }
    }

    fun deleteCategory(
        songId: String,
        categoryId: String
    ) {
        viewModelScope.launch {
            isCategoryUpdating = true
            categoriesErrorMessage = null

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
                categoriesErrorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isCategoryUpdating = false
            }
        }
    }

    fun createCategory(
        songId: String,
        categoryName: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            isCategoryUpdating = true
            categoriesErrorMessage = null

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
                categoriesErrorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isCategoryUpdating = false
            }
        }
    }

    fun deleteSong(
        songId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isDeleting = true
            errorMessage = null

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
                categoriesErrorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isDeleting = false
            }
        }
    }
}