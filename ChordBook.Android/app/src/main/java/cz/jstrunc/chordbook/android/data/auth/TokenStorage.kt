import android.content.Context

class TokenStorage(
    context: Context
) {
    private val preferences = context.getSharedPreferences(
        "auth_preferences",
        Context.MODE_PRIVATE
    )

    fun saveToken(token: String) {
        preferences.edit()
            .putString(TOKEN_KEY, token)
            .apply()
    }

    fun getToken(): String? {
        return preferences.getString(TOKEN_KEY, null)
    }

    fun clearToken() {
        preferences.edit()
            .remove(TOKEN_KEY)
            .apply()
    }

    private companion object {
        const val TOKEN_KEY = "access_token"
    }
}