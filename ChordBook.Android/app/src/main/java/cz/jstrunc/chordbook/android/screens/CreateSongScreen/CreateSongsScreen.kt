package cz.jstrunc.chordbook.android.screens.createsong

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.jstrunc.chordbook.android.screens.common.ConnectionErrorScreen
import cz.jstrunc.chordbook.android.ui.theme.ChordBookColors

/**
 * displays the form for creating a new song.
 */
@Composable
fun CreateSongScreen(
    onBackClick: () -> Unit,
    onSongCreated: (String) -> Unit,
    createSongViewModel: CreateSongViewModel = viewModel()
) {
    if (createSongViewModel.connectionErrorMessage != null) {
        ConnectionErrorScreen(
            message = createSongViewModel.connectionErrorMessage!!,
            onRetry = {
                createSongViewModel.createSong(onSongCreated)
            }
        )
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zpět",
                    tint = ChordBookColors.Primary
                )
            }
        }

        Text(
            text = "Nová písnička",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ChordBookColors.TextPrimary
        )

        Text(
            text = "Zadej základní údaje. Text a akordy doplníš později.",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        OutlinedTextField(
            value = createSongViewModel.title,
            onValueChange = createSongViewModel::onTitleChange,
            label = {
                Text("Název písničky")
            },
            singleLine = true,
            enabled = !createSongViewModel.isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChordBookColors.Primary,
                focusedLabelColor = ChordBookColors.Primary,
                cursorColor = ChordBookColors.Primary
            )
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = createSongViewModel.artist,
            onValueChange = createSongViewModel::onArtistChange,
            label = {
                Text("Interpret")
            },
            supportingText = {
                Text("Interpret je nepovinný.")
            },
            singleLine = true,
            enabled = !createSongViewModel.isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChordBookColors.Primary,
                focusedLabelColor = ChordBookColors.Primary,
                cursorColor = ChordBookColors.Primary
            )
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        createSongViewModel.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Button(
            onClick = {
                createSongViewModel.createSong(
                    onSuccess = onSongCreated
                )
            },
            enabled =
                createSongViewModel.title.isNotBlank() &&
                        !createSongViewModel.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ChordBookColors.Primary
            )
        ) {
            Text(
                text = if (createSongViewModel.isLoading) {
                    "Vytvářím..."
                } else {
                    "Vytvořit písničku"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}