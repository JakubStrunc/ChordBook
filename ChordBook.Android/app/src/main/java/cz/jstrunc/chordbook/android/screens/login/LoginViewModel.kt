package cz.jstrunc.chordbook.android.screens.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jstrunc.chordbook.android.data.api.ApiClient
import cz.jstrunc.chordbook.android.data.api.LoginRequest
import cz.jstrunc.chordbook.android.data.api.getApiErrorMessage
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * manages the state and authentication for the login screen
 */
class LoginViewModel : ViewModel() {

    /// entered username
    var username by mutableStateOf("")
        private set

    /// entered password
    var password by mutableStateOf("")
        private set

    /// indicates whether login is in progress
    var isLoading by mutableStateOf(false)
        private set

    /// authentication token
    var token by mutableStateOf<String?>(null)
        private set

    /// login error message
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /**
     * updates the username
     */
    fun onUsernameChange(value: String) {
        username = value
        errorMessage = null
    }

    /**
     * updates the password
     */
    fun onPasswordChange(value: String) {
        password = value
        errorMessage = null
    }

    /// network connection error
    var connectionErrorMessage by mutableStateOf<String?>(null)
        private set

    /**
     * authenticates the user and stores the received JWT token
     */
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
            connectionErrorMessage = null


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
            } catch (exception: HttpException) {

                errorMessage = when (exception.code()) {
                    400, 401 ->
                        "Nesprávné uživatelské jméno nebo heslo."

                    else ->
                        getApiErrorMessage(exception)
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