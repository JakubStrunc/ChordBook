package cz.jstrunc.chordbook.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.jstrunc.chordbook.android.data.api.ApiClient
import cz.jstrunc.chordbook.android.screens.createsong.CreateSongScreen
import cz.jstrunc.chordbook.android.screens.login.LoginScreen
import cz.jstrunc.chordbook.android.screens.songdetail.SongDetailScreen
import cz.jstrunc.chordbook.android.screens.songedit.EditSongScreen
import cz.jstrunc.chordbook.android.screens.songs.SongsScreen
import cz.jstrunc.chordbook.android.ui.theme.ChordBookAndroidTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        ApiClient.initialize(applicationContext)

        setContent {
            ChordBookAndroidTheme {

                var isLoggedIn by remember {
                    mutableStateOf(ApiClient.getToken() != null)
                }

                var selectedSongId by remember {
                    mutableStateOf<String?>(null)
                }

                var isCreateSongVisible by remember {
                    mutableStateOf(false)
                }

                var isEditSongVisible by remember {
                    mutableStateOf(false)
                }

                var songsRefreshKey by remember {
                    mutableStateOf(0)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    when {
                        !isLoggedIn -> {
                            LoginScreen(
                                modifier = Modifier.padding(innerPadding),
                                onLoginSuccess = {
                                    isLoggedIn = true
                                }
                            )
                        }

                        isCreateSongVisible -> {
                            CreateSongScreen(
                                onBackClick = {
                                    isCreateSongVisible = false
                                },
                                onSongCreated = { songId ->
                                    isCreateSongVisible = false
                                    selectedSongId = songId
                                }
                            )
                        }

                        isEditSongVisible &&
                                selectedSongId != null -> {

                            EditSongScreen(
                                songId = selectedSongId!!,
                                onBackClick = {
                                    isEditSongVisible = false
                                },
                                onSongSaved = {
                                    isEditSongVisible = false
                                    songsRefreshKey++
                                }
                            )
                        }

                        selectedSongId != null -> {
                            SongDetailScreen(
                                songId = selectedSongId!!,
                                onBackClick = {
                                    selectedSongId = null
                                },
                                onSongDeleted = {
                                    selectedSongId = null
                                    songsRefreshKey++
                                },
                                onEditClick = {
                                    isEditSongVisible = true
                                }
                            )
                        }

                        else -> {
                            SongsScreen(
                                modifier = Modifier.padding(innerPadding),
                                refreshKey = songsRefreshKey,
                                onSongClick = { songId ->
                                    selectedSongId = songId
                                },
                                onAddSongClick = {
                                    isCreateSongVisible = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}