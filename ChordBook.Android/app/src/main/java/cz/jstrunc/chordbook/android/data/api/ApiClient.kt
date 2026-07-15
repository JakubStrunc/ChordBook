package cz.jstrunc.chordbook.android.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:5085/"

    val authApi: AuthApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApi::class.java)
}