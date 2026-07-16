import android.content.Context
import androidx.core.content.edit

/**
 * stores the authentication token
 */
class TokenStorage(
    context: Context
) {
    private val preferences = context.getSharedPreferences(
        "auth_preferences",
        Context.MODE_PRIVATE
    )

    /**
     * saves the authentication token
     */
    fun saveToken(token: String) {
        preferences.edit {
            putString(TOKEN_KEY, token)
        }
    }

    /**
     * returns the stored authentication token
     */
    fun getToken(): String? {
        return preferences.getString(TOKEN_KEY, null)
    }

    /**
     * removes the stored authentication token
     */
    fun clearToken() {
        preferences.edit {
            remove(TOKEN_KEY)
        }
    }

    private companion object {
        const val TOKEN_KEY = "access_token"
    }
}