package cz.jstrunc.chordbook.android.data.auth

import TokenStorage
import okhttp3.Interceptor
import okhttp3.Response

/**
 * adds the JWT authentication token to every outgoing API request
 */
class AuthInterceptor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    /**
     * adds the Authorization header when a token is available
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenStorage.getToken()

        val request = if (token.isNullOrBlank()) {
            chain.request()
        } else {
            chain.request()
                .newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer $token"
                )
                .build()
        }

        return chain.proceed(request)
    }
}