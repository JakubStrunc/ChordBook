package cz.jstrunc.chordbook.android.data.api

import TokenStorage
import android.content.Context
import cz.jstrunc.chordbook.android.data.auth.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:5085/"

    private lateinit var tokenStorage: TokenStorage

    lateinit var authApi: AuthApi
        private set

    lateinit var categoriesApi: CategoriesApi
        private set

    lateinit var chordsApi: ChordsApi
        private set

    lateinit var songsApi: SongsApi
        private set

    fun initialize(context: Context) {
        tokenStorage = TokenStorage(context.applicationContext)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor(tokenStorage)
            )
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authApi = retrofit.create(AuthApi::class.java)
        categoriesApi = retrofit.create(CategoriesApi::class.java)
        songsApi = retrofit.create(SongsApi::class.java)
        chordsApi = retrofit.create(ChordsApi::class.java)
    }

    fun saveToken(token: String) {
        tokenStorage.saveToken(token)
    }

    fun clearToken() {
        tokenStorage.clearToken()
    }

    fun getToken(): String? {
        return tokenStorage.getToken()
    }
}