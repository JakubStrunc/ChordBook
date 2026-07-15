package cz.jstrunc.chordbook.android.data.auth

import TokenStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenStorage: TokenStorage
) : Interceptor {

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