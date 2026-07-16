package cz.jstrunc.chordbook.android.screens.songedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jstrunc.chordbook.android.data.api.ApiClient
import cz.jstrunc.chordbook.android.data.api.ChordResponse
import cz.jstrunc.chordbook.android.data.api.CreateChordRequest
import cz.jstrunc.chordbook.android.data.api.SongDetailResponse
import cz.jstrunc.chordbook.android.data.api.UpdateChordPositionRequest
import cz.jstrunc.chordbook.android.data.api.UpdateSongLineRequest
import cz.jstrunc.chordbook.android.data.api.UpdateSongRequest
import cz.jstrunc.chordbook.android.data.api.getApiErrorMessage
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SongEditViewModel : ViewModel() {

    var song by mutableStateOf<SongDetailResponse?>(null)
        private set

    var chords by mutableStateOf<List<ChordResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var areChordsLoading by mutableStateOf(false)
        private set

    var isChordSaving by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var chordErrorMessage by mutableStateOf<String?>(null)
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

    fun loadChords() {
        viewModelScope.launch {
            areChordsLoading = true
            chordErrorMessage = null

            try {
                chords = ApiClient.chordsApi
                    .getChordsRequest()
                    .sortedBy { chord ->
                        chord.name.lowercase()
                    }
            } catch (exception: HttpException) {
                chordErrorMessage = when (exception.code()) {
                    401 -> "Pro načtení akordů se musíte přihlásit"
                    else -> "Akordy se nepodařilo načíst"
                }
            } catch (exception: Exception) {
                errorMessage =
                    getApiErrorMessage(exception)
            } finally {
                areChordsLoading = false
            }
        }
    }

    fun createChord(
        name: String,
        fingering: String,
        onSuccess: (ChordResponse) -> Unit
    ) {
        if (name.isBlank()) {
            chordErrorMessage =
                "Název akordu nesmí být prázdný"
            return
        }

        if (fingering.isBlank()) {
            chordErrorMessage =
                "Prstoklad nesmí být prázdný"
            return
        }

        viewModelScope.launch {
            isChordSaving = true
            chordErrorMessage = null

            try {
                val createdChord = ApiClient.chordsApi
                    .createChordRequest(
                        CreateChordRequest(
                            name = name.trim(),
                            fingering = fingering.trim()
                        )
                    )

                chords = chords
                    .plus(createdChord)
                    .distinctBy { chord ->
                        chord.id
                    }
                    .sortedBy { chord ->
                        chord.name.lowercase()
                    }

                onSuccess(createdChord)
            } catch (exception: HttpException) {
                chordErrorMessage = when (exception.code()) {
                    400 -> "Zadaný akord není platný"
                    409 -> "Akord s tímto názvem už existuje"
                    else -> "Akord se nepodařilo vytvořit"
                }
            } catch (exception: Exception) {
                errorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isChordSaving = false
            }
        }
    }

    fun deleteChord(
        chordId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isChordSaving = true
            chordErrorMessage = null

            try {
                ApiClient.chordsApi
                    .deleteChordRequest(chordId)

                chords = chords.filterNot { chord ->
                    chord.id == chordId
                }

                onSuccess()
            } catch (exception: HttpException) {
                chordErrorMessage = when (exception.code()) {
                    404 -> "Akord už neexistuje"

                    409 ->
                        "Akord nelze smazat, protože je použitý v některé písničce"

                    else -> "Akord se nepodařilo smazat"
                }
            } catch (exception: Exception) {
                errorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isChordSaving = false
            }
        }
    }

    fun clearChordError() {
        chordErrorMessage = null
    }

    fun saveSong(
        songId: String,
        draft: EditableSongDraft,
        onSuccess: () -> Unit
    ) {
        if (draft.title.isBlank()) {
            errorMessage =
                "Název písničky nesmí být prázdný"
            return
        }

        viewModelScope.launch {
            isSaving = true
            errorMessage = null

            try {
                val request = UpdateSongRequest(
                    title = draft.title.trim(),
                    artist = draft.artist
                        ?.trim()
                        ?.ifBlank { null },
                    lines = draft.lines.map { line ->
                        UpdateSongLineRequest(
                            lineNumber = line.lineNumber,
                            text = line.text,
                            chords = line.chords.map { position ->
                                UpdateChordPositionRequest(
                                    characterIndex =
                                        position.characterIndex,
                                    chordId =
                                        position.chord.id
                                )
                            }
                        )
                    }
                )

                song = ApiClient.songsApi
                    .updateSongRequest(
                        songId = songId,
                        request = request
                    )

                onSuccess()
            } catch (exception: HttpException) {
                errorMessage = when (exception.code()) {
                    400 -> "Odeslaná data nejsou platná"
                    404 -> "Písnička nebyla nalezena"
                    else -> "Písničku se nepodařilo uložit"
                }
            } catch (exception: Exception) {
                errorMessage =
                    getApiErrorMessage(exception)
            } finally {
                isSaving = false
            }
        }
    }
}