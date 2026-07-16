package cz.jstrunc.chordbook.android.data.api

import TokenStorage
import android.content.Context
import cz.jstrunc.chordbook.android.data.auth.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * provides access to all backend API endpoints
 *
 *  - initializes Retrofit,
 *  - configures authentication
 *  - exposes API interfaces
 */
object ApiClient {

    /// base URL of the backend API
    private const val BASE_URL = "https://chordbook-jstrunc-api-g9cg9ybegxhvdvbu.germanywestcentral-01.azurewebsites.net/"

    /// stores the JWT token on the device
    private lateinit var tokenStorage: TokenStorage

    /// authentication endpoints
    lateinit var authApi: AuthApi
        private set

    /// category endpoints
    lateinit var categoriesApi: CategoriesApi
        private set

    /// chord endpoints
    lateinit var chordsApi: ChordsApi
        private set

    /// song endpoints
    lateinit var songsApi: SongsApi
        private set

    /**
     * initializes the API client
     */
    fun initialize(context: Context) {

        tokenStorage = TokenStorage(context.applicationContext)

        // configures the authentication interceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor(tokenStorage)
            )
            .build()

        // creates the Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // initializes all APIs
        authApi = retrofit.create(AuthApi::class.java)
        categoriesApi = retrofit.create(CategoriesApi::class.java)
        songsApi = retrofit.create(SongsApi::class.java)
        chordsApi = retrofit.create(ChordsApi::class.java)
    }

    /**
     * saves the authentication token
     */
    fun saveToken(token: String) {
        tokenStorage.saveToken(token)
    }

    /**
     * removes the stored authentication token
     */
    fun clearToken() {
        tokenStorage.clearToken()
    }

    /**
     * returns currently stored authentication token
     */
    fun getToken(): String? {
        return tokenStorage.getToken()
    }
}