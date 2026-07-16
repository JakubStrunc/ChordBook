package cz.jstrunc.chordbook.android.screens.songs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.jstrunc.chordbook.android.data.api.CategoryResponse
import cz.jstrunc.chordbook.android.data.api.SongListItemResponse
import cz.jstrunc.chordbook.android.screens.SongsScreen.SongsViewModel
import cz.jstrunc.chordbook.android.ui.theme.ChordBookAndroidTheme
import cz.jstrunc.chordbook.android.ui.theme.ChordBookColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsScreen(
    modifier: Modifier = Modifier,
    refreshKey: Int,
    songsViewModel: SongsViewModel = viewModel(),
    onSongClick: (String) -> Unit = {},
    onAddSongClick: () -> Unit = {}
) {

    LaunchedEffect(refreshKey) {
        songsViewModel.loadSongs()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(ChordBookColors.Primary)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Písničky",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSongClick,
                containerColor = ChordBookColors.Primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Přidat písničku"
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = songsViewModel.searchText,
                    onValueChange = songsViewModel::onSearchTextChange,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (songsViewModel.searchText.isNotEmpty()) {
                            IconButton(
                                onClick = songsViewModel::clearSearch
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Vymazat vyhledávání"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChordBookColors.Primary,
                        focusedLabelColor = ChordBookColors.Primary,
                        cursorColor = ChordBookColors.Primary
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = songsViewModel::toggleFilters
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = ChordBookColors.Primary
                    )

                    Text(
                        text = "Filtry",
                        color = ChordBookColors.Primary
                    )
                }
            }

            AnimatedVisibility(
                visible = songsViewModel.isFilterDialogVisible
            ) {
                CategoriesFilterPanel(
                    categories = songsViewModel.categories,
                    selectedCategoryIds = songsViewModel.selectedCategoryIds,
                    errorMessage = songsViewModel.categoriesErrorMessage,
                    onCategoryClick = songsViewModel::toggleCategory,
                    onClear = songsViewModel::clearFilters
                )
            }

            when {
                songsViewModel.isSongsLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = ChordBookColors.Primary
                        )
                    }
                }

                songsViewModel.songsErrorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = songsViewModel.songsErrorMessage
                                ?: "Písničky se nepodařilo načíst.",
                            color = MaterialTheme.colorScheme.error
                        )

                        TextButton(
                            onClick = songsViewModel::loadSongs
                        ) {
                            Text("Zkusit znovu")
                        }
                    }
                }

                songsViewModel.songs.isEmpty() -> {
                    EmptySongsMessage()
                }

                else -> {
                    Text(
                        text = "Nalezeno: ${songsViewModel.songs.size}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            top = 8.dp,
                            bottom = 4.dp
                        )
                    )

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = songsViewModel.songs,
                            key = { song -> song.id }
                        ) { song ->
                            SongCard(
                                song = song,
                                onClick = {
                                    onSongClick(song.id)
                                }
                            )
                        }

                        item {
                            Spacer(
                                modifier = Modifier.height(80.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SongCard(
    song: SongListItemResponse,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = ChordBookColors.Primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = ChordBookColors.Primary,
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = song.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = ChordBookColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = song.artist ?: "Neznámý interpret",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = "Zobrazit text a akordy",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        IconButton(
            onClick = onClick
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Otevřít písničku",
                tint = ChordBookColors.Primary
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
private fun EmptySongsMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Žádné písničky nebyly nalezeny.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "Zkus změnit hledaný výraz.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun CategoriesFilterPanel(
    categories: List<CategoryResponse>,
    selectedCategoryIds: Set<String>,
    errorMessage: String?,
    onCategoryClick: (String) -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Kategorie",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            if (selectedCategoryIds.isNotEmpty()) {
                TextButton(
                    onClick = onClear
                ) {
                    Text("Vymazat")
                }
            }
        }

        when {
            errorMessage != null -> {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            categories.isEmpty() -> {
                Text(
                    text = "Žádné kategorie.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 180.dp)
                ) {
                    items(
                        items = categories,
                        key = { category -> category.id }
                    ) { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCategoryClick(category.id)
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = category.id in selectedCategoryIds,
                                onCheckedChange = {
                                    onCategoryClick(category.id)
                                }
                            )

                            Text(
                                text = category.name,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SortOptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}



@Preview(showBackground = true)
@Composable
private fun SongsScreenPreview() {
    ChordBookAndroidTheme {
        SongsScreen(
            refreshKey = 0
        )
    }
}