package cz.jstrunc.chordbook.android.screens.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.jstrunc.chordbook.android.data.api.ApiClient
import cz.jstrunc.chordbook.android.data.api.LoginRequest
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var token by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onUsernameChange(value: String) {
        username = value
        errorMessage = null
    }

    fun onPasswordChange(value: String) {
        password = value
        errorMessage = null
    }

    fun login(
        onSuccess: () -> Unit
    ) {

        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Vyplň uživatelské jméno a heslo."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = ApiClient.authApi.login(
                    LoginRequest(
                        username,
                        password
                    )
                )
                val receivedToken = response.accessToken

                if (receivedToken.isBlank()) {
                    errorMessage = "Server nevrátil přihlašovací token."
                    return@launch
                }

                ApiClient.saveToken(receivedToken)
                token = receivedToken

                onSuccess()
            } catch (exception: Exception) {
                Log.e("LOGIN", "Login failed", exception)
                errorMessage = "Přihlášení se nezdařilo."
            } finally {
                isLoading = false
            }
        }
    }
}