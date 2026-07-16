package cz.jstrunc.chordbook.android.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.jstrunc.chordbook.android.screens.common.ConnectionErrorScreen
import cz.jstrunc.chordbook.android.ui.theme.ChordBookAndroidTheme
import cz.jstrunc.chordbook.android.ui.theme.ChordBookColors


/**
 * displays the login form and handles user authentication
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit
)
{

    loginViewModel.connectionErrorMessage?.let { message ->
        ConnectionErrorScreen(
            message = message,
            onRetry = {
                loginViewModel.login(
                    onSuccess = onLoginSuccess
                )
            },
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ChordBook",
            fontSize = 54.sp,
            fontWeight = FontWeight.Bold,
            color = ChordBookColors.TextPrimary,
            modifier = Modifier
                .padding(bottom = 64.dp)
        )

        OutlinedTextField(
            value = loginViewModel.username,
            onValueChange = loginViewModel::onUsernameChange,
            label = {
                Text("Uživatelské jméno")
            },
            enabled = !loginViewModel.isLoading,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.8f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChordBookColors.Primary,
                focusedLabelColor = ChordBookColors.Primary,
                cursorColor = ChordBookColors.Primary
            )
        )
        OutlinedTextField(
            value = loginViewModel.password,
            onValueChange = loginViewModel::onPasswordChange,
            label = {
                Text("Heslo")
            },
            enabled = !loginViewModel.isLoading,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(0.8f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChordBookColors.Primary,
                focusedLabelColor = ChordBookColors.Primary,
                cursorColor = ChordBookColors.Primary
            )


        )
        Button(
            onClick = {
                loginViewModel.login(
                    onSuccess = onLoginSuccess
                )
            },
            enabled = !loginViewModel.isLoading,
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(
                containerColor = ChordBookColors.Primary,
                contentColor = Color.White
            )
        ) {
            Text(
                text = if (loginViewModel.isLoading) {
                    "Přihlašuji..."
                } else {
                    "Přihlásit se"
                }
            )
        }

        loginViewModel.errorMessage?.let { message ->
            Text(
                text = message,
                color = Color.White,

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
                    .background(
                        color = Color.Red.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)

                    )
                    .padding(12.dp)


            )
        }
    }

}


/**
 * preview of the login screen
 */
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ChordBookAndroidTheme {
        LoginScreen(
            onLoginSuccess = {}
        )
    }
}