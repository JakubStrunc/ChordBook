package cz.jstrunc.chordbook.android.screens.songedit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.jstrunc.chordbook.android.data.api.ChordPositionResponse
import cz.jstrunc.chordbook.android.data.api.ChordResponse
import cz.jstrunc.chordbook.android.data.api.SongChordResponse
import cz.jstrunc.chordbook.android.data.api.SongDetailResponse
import cz.jstrunc.chordbook.android.ui.theme.ChordBookColors

enum class SongEditTab(val title: String) {
    DETAILS("Údaje"),
    LYRICS("Text"),
    CHORDS("Akordy")
}

data class EditableSongLine(
    val lineNumber: Int,
    val text: String,
    val chords: List<ChordPositionResponse>
)

data class EditableSongDraft(
    val title: String,
    val artist: String?,
    val lines: List<EditableSongLine>
)

@Composable
fun EditSongScreen(
    songId: String,
    onBackClick: () -> Unit,
    onSongSaved: () -> Unit,
    modifier: Modifier = Modifier,
    songEditViewModel: SongEditViewModel = viewModel()
) {
    LaunchedEffect(songId) {
        songEditViewModel.loadSong(songId)
        songEditViewModel.loadChords()
    }

    when {
        songEditViewModel.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = ChordBookColors.Primary
                )
            }
        }

        songEditViewModel.song == null -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = songEditViewModel.errorMessage
                        ?: "Písničku se nepodařilo načíst.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        else -> {
            EditSongContent(
                song = songEditViewModel.song!!,
                availableChords = songEditViewModel.chords,
                isLoadingChords = songEditViewModel.areChordsLoading,
                isSaving = songEditViewModel.isSaving,
                isChordSaving = songEditViewModel.isChordSaving,
                errorMessage = songEditViewModel.errorMessage,
                chordErrorMessage = songEditViewModel.chordErrorMessage,
                onBackClick = onBackClick,
                onCreateChord = { name, fingering, onCreated ->
                    songEditViewModel.createChord(
                        name = name,
                        fingering = fingering,
                        onSuccess = onCreated
                    )
                },
                onDeleteChord = { chordId, onDeleted ->
                    songEditViewModel.deleteChord(
                        chordId = chordId,
                        onSuccess = onDeleted
                    )
                },
                onClearChordError = {
                    songEditViewModel.clearChordError()
                },
                onSave = { draft ->
                    songEditViewModel.saveSong(
                        songId = songId,
                        draft = draft,
                        onSuccess = onSongSaved
                    )
                },
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSongContent(
    song: SongDetailResponse,
    availableChords: List<ChordResponse>,
    isLoadingChords: Boolean,
    isSaving: Boolean,
    isChordSaving: Boolean,
    errorMessage: String?,
    chordErrorMessage: String?,
    onBackClick: () -> Unit,
    onCreateChord: (
        name: String,
        fingering: String,
        onCreated: (ChordResponse) -> Unit
    ) -> Unit,
    onDeleteChord: (
        chordId: String,
        onDeleted: () -> Unit
    ) -> Unit,
    onClearChordError: () -> Unit,
    onSave: (EditableSongDraft) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember {
        mutableStateOf(SongEditTab.DETAILS)
    }

    var title by remember(song.id) {
        mutableStateOf(song.title)
    }

    var artist by remember(song.id) {
        mutableStateOf(song.artist.orEmpty())
    }

    var editableLines by remember(song.id) {
        mutableStateOf(
            song.lines
                .sortedBy { line -> line.lineNumber }
                .map { line ->
                    EditableSongLine(
                        lineNumber = line.lineNumber,
                        text = line.text,
                        chords = line.chords
                    )
                }
        )
    }

    var lyricsText by remember(song.id) {
        mutableStateOf(
            editableLines.joinToString("\n") { line ->
                line.text
            }
        )
    }

    var selectedLineNumber by remember(song.id) {
        mutableStateOf<Int?>(null)
    }

    var selectedCharacterIndex by remember(song.id) {
        mutableStateOf<Int?>(null)
    }

    var isChordPickerVisible by remember(song.id) {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        EditSongHeader(
            isSaving = isSaving,
            saveEnabled = title.isNotBlank(),
            onBackClick = onBackClick,
            onSaveClick = {
                onSave(
                    EditableSongDraft(
                        title = title.trim(),
                        artist = artist.trim().ifBlank { null },
                        lines = editableLines
                    )
                )
            }
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            )
        }

        EditSongTabs(
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                selectedTab = tab
            }
        )

        when (selectedTab) {
            SongEditTab.DETAILS -> {
                DetailsEditTab(
                    title = title,
                    artist = artist,
                    isEnabled = !isSaving,
                    onTitleChange = { title = it },
                    onArtistChange = { artist = it }
                )
            }

            SongEditTab.LYRICS -> {
                LyricsEditTab(
                    lyricsText = lyricsText,
                    isEnabled = !isSaving,
                    onLyricsChange = { newLyrics ->
                        lyricsText = newLyrics
                        editableLines = createLinesFromText(
                            text = newLyrics,
                            previousLines = editableLines
                        )
                    }
                )
            }

            SongEditTab.CHORDS -> {
                ChordsEditTab(
                    lines = editableLines,
                    isEnabled = !isSaving,
                    onCharacterClick = { lineNumber, characterIndex ->
                        selectedLineNumber = lineNumber
                        selectedCharacterIndex = characterIndex
                        isChordPickerVisible = true
                    }
                )
            }
        }
    }

    if (isChordPickerVisible) {
        val lineNumber = selectedLineNumber
        val characterIndex = selectedCharacterIndex

        if (lineNumber != null && characterIndex != null) {
            val existingPosition = editableLines
                .firstOrNull { line ->
                    line.lineNumber == lineNumber
                }
                ?.chords
                ?.firstOrNull { position ->
                    position.characterIndex == characterIndex
                }

            ModalBottomSheet(
                onDismissRequest = {
                    isChordPickerVisible = false
                    onClearChordError()
                },
                sheetState = sheetState
            ) {
                ChordPickerSheet(
                    chords = availableChords,
                    isLoading = isLoadingChords,
                    isChordSaving = isChordSaving,
                    chordErrorMessage = chordErrorMessage,
                    existingPosition = existingPosition,
                    onChordSelected = { selectedChord ->
                        editableLines = putChordAtPosition(
                            lines = editableLines,
                            lineNumber = lineNumber,
                            characterIndex = characterIndex,
                            chord = selectedChord
                        )

                        isChordPickerVisible = false
                        onClearChordError()
                    },
                    onRemoveChord = {
                        editableLines = removeChordAtPosition(
                            lines = editableLines,
                            lineNumber = lineNumber,
                            characterIndex = characterIndex
                        )

                        isChordPickerVisible = false
                        onClearChordError()
                    },
                    onCreateChord = { name, fingering ->
                        onCreateChord(
                            name,
                            fingering
                        ) { createdChord ->
                            editableLines = putChordAtPosition(
                                lines = editableLines,
                                lineNumber = lineNumber,
                                characterIndex = characterIndex,
                                chord = createdChord
                            )

                            isChordPickerVisible = false
                            onClearChordError()
                        }
                    },
                    onDeleteChord = { chordId, onDeleted ->
                        onDeleteChord(
                            chordId,
                            onDeleted
                        )
                    },
                    onClearChordError = onClearChordError
                )
            }
        }
    }
}

@Composable
private fun EditSongHeader(
    isSaving: Boolean,
    saveEnabled: Boolean,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            enabled = !isSaving
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Zpět",
                tint = ChordBookColors.Primary
            )
        }

        Text(
            text = "Upravit písničku",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onSaveClick,
            enabled = saveEnabled && !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = ChordBookColors.Primary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Uložit",
                    tint = ChordBookColors.Primary
                )
            }
        }
    }

    HorizontalDivider()
}

@Composable
private fun EditSongTabs(
    selectedTab: SongEditTab,
    onTabSelected: (SongEditTab) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal
    ) {
        SongEditTab.entries.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = {
                    onTabSelected(tab)
                },
                text = {
                    Text(
                        text = tab.title,
                        fontWeight = if (selectedTab == tab) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )
                },
                selectedContentColor = ChordBookColors.Primary,
                unselectedContentColor =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailsEditTab(
    title: String,
    artist: String,
    isEnabled: Boolean,
    onTitleChange: (String) -> Unit,
    onArtistChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Základní údaje",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = {
                Text("Název písničky")
            },
            singleLine = true,
            enabled = isEnabled,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = artist,
            onValueChange = onArtistChange,
            label = {
                Text("Interpret")
            },
            singleLine = true,
            enabled = isEnabled,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LyricsEditTab(
    lyricsText: String,
    isEnabled: Boolean,
    onLyricsChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Text písničky",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Každý nový řádek vytvoří samostatný řádek písničky.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme
                        .surfaceVariant
                        .copy(alpha = 0.35f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            BasicTextField(
                value = lyricsText,
                onValueChange = onLyricsChange,
                enabled = isEnabled,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    lineHeight = 25.sp
                ),
                cursorBrush = SolidColor(
                    ChordBookColors.Primary
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (lyricsText.isEmpty()) {
                            Text(
                                text = "Napište text písničky…",
                                color = MaterialTheme.colorScheme
                                    .onSurfaceVariant,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp
                            )
                        }

                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
private fun ChordsEditTab(
    lines: List<EditableSongLine>,
    isEnabled: Boolean,
    onCharacterClick: (
        lineNumber: Int,
        characterIndex: Int
    ) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Editor akordů",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Kliknutím do textu přidáte, změníte nebo odstraníte akord.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                top = 4.dp,
                bottom = 20.dp
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            lines.forEach { line ->
                EditableChordLine(
                    line = line,
                    isEnabled = isEnabled,
                    onCharacterClick = { characterIndex ->
                        onCharacterClick(
                            line.lineNumber,
                            characterIndex
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun EditableChordLine(
    line: EditableSongLine,
    isEnabled: Boolean,
    onCharacterClick: (Int) -> Unit
) {
    var textLayoutResult by remember(line.text) {
        mutableStateOf<TextLayoutResult?>(null)
    }

    val chordLine = buildChordLine(
        textLength = line.text.length,
        chordPositions = line.chords
    )

    Column {
        Text(
            text = chordLine.ifEmpty { " " },
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ChordBookColors.Primary,
            maxLines = 1
        )

        Text(
            text = line.text.ifEmpty { " " },
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp,
            maxLines = 1,
            onTextLayout = { result ->
                textLayoutResult = result
            },
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme
                        .surfaceVariant
                        .copy(alpha = 0.25f),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(
                    horizontal = 6.dp,
                    vertical = 5.dp
                )
                .pointerInput(
                    line.text,
                    isEnabled
                ) {
                    if (isEnabled) {
                        detectTapGestures { offset ->
                            val rawIndex = textLayoutResult
                                ?.getOffsetForPosition(offset)
                                ?: return@detectTapGestures

                            val characterIndex = rawIndex.coerceIn(
                                0,
                                line.text.length
                            )

                            onCharacterClick(characterIndex)
                        }
                    }
                }
        )
    }
}

@Composable
private fun ChordPickerSheet(
    chords: List<ChordResponse>,
    isLoading: Boolean,
    isChordSaving: Boolean,
    chordErrorMessage: String?,
    existingPosition: ChordPositionResponse?,
    onChordSelected: (ChordResponse) -> Unit,
    onRemoveChord: () -> Unit,
    onCreateChord: (
        name: String,
        fingering: String
    ) -> Unit,
    onDeleteChord: (
        chordId: String,
        onDeleted: () -> Unit
    ) -> Unit,
    onClearChordError: () -> Unit
) {
    var isCreateDialogVisible by remember {
        mutableStateOf(false)
    }

    var chordToDelete by remember {
        mutableStateOf<ChordResponse?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 8.dp,
                    top = 4.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (existingPosition == null) {
                    "Vybrat akord"
                } else {
                    "Změnit akord ${existingPosition.chord.name}"
                },
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    onClearChordError()
                    isCreateDialogVisible = true
                },
                enabled = !isChordSaving
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Přidat nový akord",
                    tint = ChordBookColors.Primary
                )
            }
        }

        if (chordErrorMessage != null) {
            Text(
                text = chordErrorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 6.dp
                )
            )
        }

        if (existingPosition != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        enabled = !isChordSaving,
                        onClick = onRemoveChord
                    )
                    .padding(
                        horizontal = 20.dp,
                        vertical = 14.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )

                Text(
                    text = "Odstranit akord z této pozice",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            HorizontalDivider()
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = ChordBookColors.Primary
                    )
                }
            }

            chords.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "V databázi nejsou žádné akordy.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = {
                            onClearChordError()
                            isCreateDialogVisible = true
                        },
                        enabled = !isChordSaving,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )

                        Text(
                            text = "Přidat první akord",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.height(360.dp)
                ) {
                    items(
                        items = chords,
                        key = { chord -> chord.id }
                    ) { chord ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    enabled = !isChordSaving
                                ) {
                                    onChordSelected(chord)
                                }
                                .padding(
                                    start = 20.dp,
                                    end = 8.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = chord.name,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChordBookColors.Primary,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = chord.fingering,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme
                                    .onSurfaceVariant
                            )

                            IconButton(
                                onClick = {
                                    onClearChordError()
                                    chordToDelete = chord
                                },
                                enabled = !isChordSaving
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription =
                                        "Smazat akord ${chord.name}",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        HorizontalDivider()
                    }
                }
            }
        }
    }

    if (isCreateDialogVisible) {
        CreateChordDialog(
            isSaving = isChordSaving,
            errorMessage = chordErrorMessage,
            onDismiss = {
                if (!isChordSaving) {
                    isCreateDialogVisible = false
                    onClearChordError()
                }
            },
            onCreate = { name, fingering ->
                onCreateChord(
                    name,
                    fingering
                )
            }
        )
    }

    chordToDelete?.let { chord ->
        AlertDialog(
            onDismissRequest = {
                if (!isChordSaving) {
                    chordToDelete = null
                    onClearChordError()
                }
            },
            title = {
                Text("Smazat akord")
            },
            text = {
                Column {
                    Text(
                        text = "Opravdu chcete akord ${chord.name} smazat z databáze?"
                    )

                    if (chordErrorMessage != null) {
                        Text(
                            text = chordErrorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteChord(chord.id) {
                            chordToDelete = null
                            onClearChordError()
                        }
                    },
                    enabled = !isChordSaving
                ) {
                    if (isChordSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Smazat",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        chordToDelete = null
                        onClearChordError()
                    },
                    enabled = !isChordSaving
                ) {
                    Text("Zrušit")
                }
            }
        )
    }
}

@Composable
private fun CreateChordDialog(
    isSaving: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onCreate: (
        name: String,
        fingering: String
    ) -> Unit
) {
    var name by remember {
        mutableStateOf("")
    }

    var fingering by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) {
                onDismiss()
            }
        },
        title = {
            Text("Přidat nový akord")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text("Název akordu")
                    },
                    placeholder = {
                        Text("Například Am")
                    },
                    singleLine = true,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fingering,
                    onValueChange = {
                        fingering = it
                    },
                    label = {
                        Text("Prstoklad")
                    },
                    placeholder = {
                        Text("Například x02210")
                    },
                    singleLine = true,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCreate(
                        name.trim(),
                        fingering.trim()
                    )
                },
                enabled = name.isNotBlank() &&
                        fingering.isNotBlank() &&
                        !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Vytvořit")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text("Zrušit")
            }
        }
    )
}

private fun putChordAtPosition(
    lines: List<EditableSongLine>,
    lineNumber: Int,
    characterIndex: Int,
    chord: ChordResponse
): List<EditableSongLine> {
    return lines.map { line ->
        if (line.lineNumber != lineNumber) {
            line
        } else {
            val newPosition = ChordPositionResponse(
                characterIndex = characterIndex,
                chord = SongChordResponse(
                    id = chord.id,
                    name = chord.name,
                    fingering = chord.fingering
                )
            )

            line.copy(
                chords = line.chords
                    .filterNot { position ->
                        position.characterIndex == characterIndex
                    }
                    .plus(newPosition)
                    .sortedBy { position ->
                        position.characterIndex
                    }
            )
        }
    }
}

private fun removeChordAtPosition(
    lines: List<EditableSongLine>,
    lineNumber: Int,
    characterIndex: Int
): List<EditableSongLine> {
    return lines.map { line ->
        if (line.lineNumber != lineNumber) {
            line
        } else {
            line.copy(
                chords = line.chords.filterNot { position ->
                    position.characterIndex == characterIndex
                }
            )
        }
    }
}

private fun createLinesFromText(
    text: String,
    previousLines: List<EditableSongLine>
): List<EditableSongLine> {
    return text
        .split("\n")
        .mapIndexed { index, lineText ->
            val previousLine = previousLines
                .firstOrNull { line ->
                    line.lineNumber == index &&
                            line.text == lineText
                }

            EditableSongLine(
                lineNumber = index,
                text = lineText,
                chords = previousLine?.chords.orEmpty()
            )
        }
}

private fun buildChordLine(
    textLength: Int,
    chordPositions: List<ChordPositionResponse>
): String {
    if (chordPositions.isEmpty()) {
        return ""
    }

    val requiredLength = chordPositions
        .maxOf { position ->
            position.characterIndex +
                    position.chord.name.length
        }
        .coerceAtLeast(textLength)

    val characters = CharArray(requiredLength) {
        ' '
    }

    chordPositions
        .sortedBy { position ->
            position.characterIndex
        }
        .forEach { position ->
            position.chord.name
                .forEachIndexed { index, character ->
                    val targetIndex =
                        position.characterIndex + index

                    if (targetIndex in characters.indices) {
                        characters[targetIndex] = character
                    }
                }
        }

    return characters
        .concatToString()
        .trimEnd()
}