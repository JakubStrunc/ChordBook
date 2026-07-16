package cz.jstrunc.chordbook.android.data.api

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * request body for user authentication
 */
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * response returned after successful authentication
 */
data class LoginResponse(
    val accessToken: String
)

/**
 * authentication API endpoints
 */
interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse
}