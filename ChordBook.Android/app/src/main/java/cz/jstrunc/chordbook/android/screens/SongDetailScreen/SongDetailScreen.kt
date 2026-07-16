package cz.jstrunc.chordbook.android.screens.songdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.jstrunc.chordbook.android.data.api.CategoryResponse
import cz.jstrunc.chordbook.android.data.api.ChordPositionResponse
import cz.jstrunc.chordbook.android.data.api.SongDetailResponse
import cz.jstrunc.chordbook.android.data.api.SongLineResponse
import cz.jstrunc.chordbook.android.screens.common.ConnectionErrorScreen
import cz.jstrunc.chordbook.android.ui.theme.ChordBookColors


/**
 * displays the detail of a selected song
 */
@Composable
fun SongDetailScreen(
    songId: String,
    onBackClick: () -> Unit,
    onSongDeleted: () -> Unit,
    onEditClick: () -> Unit,
    songDetailViewModel: SongDetailViewModel = viewModel()
) {
    LaunchedEffect(songId) {
        songDetailViewModel.loadSong(songId)
        songDetailViewModel.loadCategories()
    }

    songDetailViewModel.connectionErrorMessage?.let { message ->
        ConnectionErrorScreen(
            message = message,
            onRetry = {
                songDetailViewModel.loadSong(songId)
                songDetailViewModel.loadCategories()
            }
        )
        return
    }

    when {
        songDetailViewModel.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = ChordBookColors.Primary
                )
            }
        }

        songDetailViewModel.errorMessage != null &&
                songDetailViewModel.song == null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = songDetailViewModel.errorMessage
                        ?: "Písničku se nepodařilo načíst.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        songDetailViewModel.song != null -> {
            SongDetailContent(
                song = songDetailViewModel.song!!,
                availableCategories =
                    songDetailViewModel.availableCategories,
                isCategoriesLoading =
                    songDetailViewModel.isCategoriesLoading,
                isCategoryUpdating =
                    songDetailViewModel.isCategoryUpdating,
                categoriesErrorMessage =
                    songDetailViewModel.categoriesErrorMessage,
                isDeleting = songDetailViewModel.isDeleting,
                errorMessage = songDetailViewModel.errorMessage,

                onBackClick = onBackClick,
                onEditClick = onEditClick,

                onLoadCategories = {
                    songDetailViewModel.loadCategories()
                },

                onAddCategory = { categoryId, onSuccess ->
                    songDetailViewModel.addCategory(
                        songId = songId,
                        categoryId = categoryId,
                        onSuccess = onSuccess
                    )
                },

                onCreateCategory = { categoryName, onSuccess ->
                    songDetailViewModel.createCategory(
                        songId = songId,
                        categoryName = categoryName,
                        onSuccess = onSuccess
                    )
                },

                onRemoveCategory = { categoryId ->
                    songDetailViewModel.deleteCategory(
                        songId = songId,
                        categoryId = categoryId
                    )
                },

                onDeleteConfirm = {
                    songDetailViewModel.deleteSong(
                        songId = songId,
                        onSuccess = onSongDeleted
                    )
                }
            )
        }
    }

}


/**
 * displays the main content of the song detail screen
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SongDetailContent(
    song: SongDetailResponse,
    availableCategories: List<CategoryResponse>,
    isCategoriesLoading: Boolean,
    isCategoryUpdating: Boolean,
    categoriesErrorMessage: String?,
    isDeleting: Boolean,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onLoadCategories: () -> Unit,
    onAddCategory: (
        categoryId: String,
        onSuccess: () -> Unit
    ) -> Unit,
    onCreateCategory: (
        categoryName: String,
        onSuccess: () -> Unit
    ) -> Unit,
    onRemoveCategory: (categoryId: String) -> Unit,
    onDeleteConfirm: () -> Unit
) {
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var showCategoryDialog by remember {
        mutableStateOf(false)
    }

    val usedChords = song.lines
        .flatMap { line -> line.chords }
        .map { position -> position.chord }
        .distinctBy { chord -> chord.name }
        .sortedBy { chord -> chord.name }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            enabled = !isDeleting && !isCategoryUpdating,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Zpět",
                tint = ChordBookColors.Primary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = song.title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onEditClick,
                enabled = !isDeleting && !isCategoryUpdating
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Upravit písničku",
                    tint = ChordBookColors.Primary
                )
            }

            IconButton(
                onClick = {
                    showDeleteDialog = true
                },
                enabled = !isDeleting && !isCategoryUpdating
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Smazat písničku",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Text(
            text = song.artist ?: "Neznámý interpret",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        if (errorMessage != null) {
            ErrorText(errorMessage)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Akordy",
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            usedChords.forEach { chord ->
                ChordPill(
                    name = chord.name
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Kategorie",
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            song.categories.forEach { categoryName ->

                val categoryId = availableCategories
                    .firstOrNull { category ->
                        category.name == categoryName
                    }
                    ?.id

                CategoryPill(
                    name = categoryName,
                    enabled =
                        categoryId != null &&
                                !isCategoryUpdating,
                    onRemove = {
                        if (categoryId != null) {
                            onRemoveCategory(categoryId)
                        }
                    }
                )
            }

            AddCategoryPill(
                enabled = !isCategoryUpdating,
                onClick = {
                    onLoadCategories()
                    showCategoryDialog = true
                }
            )
        }

        if (isCategoryUpdating) {
            Row(
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = ChordBookColors.Primary
                )

                Text(
                    text = "Ukládám kategorii…",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        if (categoriesErrorMessage != null) {
            ErrorText(categoriesErrorMessage)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            song.lines
                .sortedBy { line -> line.lineNumber }
                .forEach { line ->
                    SongLyricsLine(line)
                }
        }
    }

    if (showCategoryDialog) {
        AddCategoryDialog(
            songCategoryNames = song.categories,
            availableCategories = availableCategories,
            isLoading = isCategoriesLoading,
            isUpdating = isCategoryUpdating,
            errorMessage = categoriesErrorMessage,

            onDismiss = {
                if (!isCategoryUpdating) {
                    showCategoryDialog = false
                }
            },

            onCategoryClick = { categoryId ->
                onAddCategory(categoryId) {
                    showCategoryDialog = false
                }
            },

            onCreateCategory = { categoryName ->
                onCreateCategory(categoryName) {
                    showCategoryDialog = false
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    showDeleteDialog = false
                }
            },
            title = {
                Text("Smazat písničku?")
            },
            text = {
                Text(
                    text = "Opravdu chcete smazat písničku ${song.title}?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteConfirm()
                    },
                    enabled = !isDeleting
                ) {
                    Text(
                        text = "Smazat",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    },
                    enabled = !isDeleting
                ) {
                    Text("Zrušit")
                }
            }
        )
    }
}

/**
 * displays a dialog for assigning or creating a category
 */
@Composable
private fun AddCategoryDialog(
    songCategoryNames: List<String>,
    availableCategories: List<CategoryResponse>,
    isLoading: Boolean,
    isUpdating: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onCreateCategory: (String) -> Unit
) {
    var newCategoryName by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Přidat kategorii")
        },
        text = {
            Column {
                Text(
                    text = "Existující kategorie",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = ChordBookColors.Primary
                            )
                        }
                    }

                    availableCategories.isEmpty() -> {
                        Text(
                            text = "Nejsou vytvořené žádné kategorie.",
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 220.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            availableCategories.forEach { category ->

                                val isAssigned =
                                    category.name in songCategoryNames

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            enabled =
                                                !isAssigned &&
                                                        !isUpdating
                                        ) {
                                            onCategoryClick(category.id)
                                        }
                                        .padding(
                                            horizontal = 4.dp,
                                            vertical = 12.dp
                                        ),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = category.name,
                                        fontSize = 16.sp,
                                        color = if (isAssigned) {
                                            MaterialTheme.colorScheme
                                                .onSurfaceVariant
                                        } else {
                                            MaterialTheme.colorScheme
                                                .onSurface
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (isAssigned) {
                                        Icon(
                                            imageVector =
                                                Icons.Default.Check,
                                            contentDescription =
                                                "Kategorie je přiřazená",
                                            tint = ChordBookColors.Primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Nová kategorie",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = {
                        newCategoryName = it
                    },
                    label = {
                        Text("Název kategorie")
                    },
                    singleLine = true,
                    enabled = !isUpdating,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isUpdating) {
                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = ChordBookColors.Primary
                        )

                        Text(
                            text = "Ukládám…",
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                if (errorMessage != null) {
                    ErrorText(errorMessage)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val categoryName = newCategoryName.trim()

                    if (categoryName.isNotEmpty()) {
                        onCreateCategory(categoryName)
                    }
                },
                enabled =
                    newCategoryName.isNotBlank() &&
                            !isUpdating
            ) {
                Text("Vytvořit")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isUpdating
            ) {
                Text("Zavřít")
            }
        }
    )
}

/**
 * displays an error message
 */
@Composable
private fun ErrorText(
    message: String
) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 8.dp)
    )
}

/**
 * displays a chord as a rounded label
 */
@Composable
private fun ChordPill(
    name: String
) {
    Box(
        modifier = Modifier
            .background(
                color = ChordBookColors.Primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50)
            )
            .padding(
                horizontal = 14.dp,
                vertical = 7.dp
            )
    ) {
        Text(
            text = name,
            color = ChordBookColors.Primary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

/**
 * displays an assigned category with a remove action
 */
@Composable
private fun CategoryPill(
    name: String,
    enabled: Boolean,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(50)
            )
            .padding(
                start = 14.dp,
                end = 4.dp,
                top = 4.dp,
                bottom = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        IconButton(
            onClick = onRemove,
            enabled = enabled,
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Odebrat kategorii $name",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * displays the button for adding a category
 */
@Composable
private fun AddCategoryPill(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .background(
                color = ChordBookColors.Primary.copy(
                    alpha = if (enabled) 0.12f else 0.05f
                ),
                shape = RoundedCornerShape(50)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Přidat kategorii",
            tint = ChordBookColors.Primary.copy(
                alpha = if (enabled) 1f else 0.4f
            ),
            modifier = Modifier.size(21.dp)
        )
    }
}

/**
 * displays one lyrics line with its chords
 */
@Composable
private fun SongLyricsLine(
    line: SongLineResponse
) {
    val chordLine = buildChordLine(
        textLength = line.text.length,
        chordPositions = line.chords
    )

    Column {
        if (chordLine.isNotBlank()) {
            Text(
                text = chordLine,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ChordBookColors.Primary,
                maxLines = 1
            )
        }

        Text(
            text = line.text.ifEmpty { " " },
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp,
            maxLines = 1
        )
    }
}

/**
 * builds the visual chord line displayed above the lyrics
 */
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

            position.chord.name.forEachIndexed { index, character ->

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