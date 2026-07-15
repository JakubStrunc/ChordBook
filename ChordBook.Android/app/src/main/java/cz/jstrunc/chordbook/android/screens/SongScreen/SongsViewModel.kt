package cz.jstrunc.chordbook.android.screens.SongScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cz.jstrunc.chordbook.android.data.api.SongListItemResponse

class SongsViewModel : ViewModel() {

    var searchText by mutableStateOf("")


    var isFilterDialogVisible by mutableStateOf(false)

    private val songs = listOf(
        SongListItemResponse(
            id = "6abf9881-d11d-4d7a-ae77-c01c69b67852",
            title = "Rosa na kolejích",
            artist = "Wabi Daněk"
        ),
        SongListItemResponse(
            id = "93458de9-9a83-486f-b820-519a73146bd7",
            title = "Tři kříže",
            artist = "Hop Trop"
        ),
        SongListItemResponse(
            id = "8c90a48b-d39d-415a-82ca-d6ec738ce17a",
            title = "Hlídač krav",
            artist = "Jaromír Nohavica"
        ),
        SongListItemResponse(
            id = "c23cb983-bc69-4c10-84e0-cf24325077dc",
            title = "Knockin' on Heaven's Door",
            artist = "Bob Dylan"
        ),
        SongListItemResponse(
            id = "adeae23e-ac44-460d-b163-b17f590d8948",
            title = "Neznámá píseň",
            artist = null
        )
    )

    val filteredSongs: List<SongListItemResponse>
        get() {
            val searchedSongs = songs.filter { song ->
                searchText.isBlank() ||
                        song.title.contains(
                            other = searchText,
                            ignoreCase = true
                        ) ||
                        song.artist?.contains(
                            other = searchText,
                            ignoreCase = true
                        ) == true
            }

            return searchedSongs.sortedBy { song ->
                song.title.lowercase()
            }
        }

    fun onSearchTextChange(value: String) {
        searchText = value
    }

    fun clearSearch() {
        searchText = ""
    }

    fun showFilters() {
        isFilterDialogVisible = true
    }

    fun hideFilters() {
        isFilterDialogVisible = false
    }

    fun clearFilters() {

    }


}