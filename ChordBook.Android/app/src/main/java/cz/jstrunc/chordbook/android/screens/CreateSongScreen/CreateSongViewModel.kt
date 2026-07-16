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

/**
 * manages the state and API communication for creating a new song
 */
class CreateSongViewModel : ViewModel() {

    /// song title
    var title by mutableStateOf("")
        private set

    /// song artist
    var artist by mutableStateOf("")
        private set

    /// indicates whether the song is being created
    var isLoading by mutableStateOf(false)
        private set

    /// error message displayed on the screen
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /// network connection error
    var connectionErrorMessage by mutableStateOf<String?>(null)
        private set

    /**
     * updates the song title
     */
    fun onTitleChange(value: String) {
        title = value
    }

    /**
     * updates the song artist
     */
    fun onArtistChange(value: String) {
        artist = value
    }

    /**
     * validates the input and creates a new song
     */
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
            connectionErrorMessage = null

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
                connectionErrorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isLoading = false
            }
        }
    }
}