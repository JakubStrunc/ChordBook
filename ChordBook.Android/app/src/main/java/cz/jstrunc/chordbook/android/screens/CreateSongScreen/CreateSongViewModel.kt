package cz.jstrunc.chordbook.android.screens.createsong

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jstrunc.chordbook.android.data.api.ApiClient
import cz.jstrunc.chordbook.android.data.api.CreateSongRequest
import cz.jstrunc.chordbook.android.data.api.getApiErrorMessage
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CreateSongViewModel : ViewModel() {

    var title by mutableStateOf("")

    var artist by mutableStateOf("")

    var isLoading by mutableStateOf(false)

    var errorMessage by mutableStateOf<String?>(null)

    fun onTitleChange(value: String) {
        title = value
    }

    fun onArtistChange(value: String) {
        artist = value
    }

    fun createSong(
        onSuccess: (String) -> Unit
    ) {
        val trimmedTitle = title.trim()
        val trimmedArtist = artist.trim()

        if (trimmedTitle.isBlank()) {
            errorMessage = "Název písničky je povinný."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = ApiClient.songsApi.createSongRequest(
                    CreateSongRequest(
                        title = trimmedTitle,
                        artist = trimmedArtist.ifBlank {
                            null
                        }
                    )
                )

                onSuccess(response.id)
            } catch (exception: HttpException) {
                errorMessage = when (exception.code()) {
                    400 -> "Zadané údaje nejsou platné."
                    401 -> "Pro vytvoření písničky se musíš přihlásit."
                    else -> getApiErrorMessage(exception)
                }
            } catch (exception: Exception) {
                errorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isLoading = false
            }
        }
    }
}