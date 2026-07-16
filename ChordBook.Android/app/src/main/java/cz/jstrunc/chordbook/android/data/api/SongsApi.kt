package cz.jstrunc.chordbook.android.data.api


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * song returned in the song list
 */
data class SongListItemResponse(
    val id: String,
    val title: String,
    val artist: String?
)

/**
 * detailed song returned by the API
 */
data class SongDetailResponse(
    val id: String,
    val title: String,
    val artist: String?,
    val categories: List<String>,
    val lines: List<SongLineResponse>
)

data class SongLineResponse(
    val lineNumber: Int,
    val text: String,
    val chords: List<ChordPositionResponse>
)

/**
 * song line returned by the API
 */
data class ChordPositionResponse(
    val characterIndex: Int,
    val chord: SongChordResponse
)

/**
 * chord position within a song line
 */
data class SongChordResponse(
    val id: String,
    val name: String,
    val fingering: String?
)

data class CreateSongRequest(
    val title: String,
    val artist: String?
)

/**
 * chord returned as part of a song
 */
data class CreateSongResponse(
    val id: String
)

/**
 * request for creating a new song
 */
data class UpdateSongRequest(
    val title: String,
    val artist: String?,
    val lines: List<UpdateSongLineRequest>
)

/**
 * response returned after creating a song
 */
data class UpdateSongLineRequest(
    val lineNumber: Int,
    val text: String,
    val chords: List<UpdateChordPositionRequest>
)


/**
 * song API endpoints
 */
interface SongsApi {

    @GET("api/songs")
    suspend fun getSongsRequest(
        @Query("search") search: String? = null,
        @Query("categoryId") categoryIds: List<String> = emptyList()
    ): List<SongListItemResponse>

    @GET("api/songs/{id}")
    suspend fun getSongDetailRequest(
        @Path("id") songId: String
    ): SongDetailResponse

    @POST("api/songs")
    suspend fun createSongRequest(
        @Body request: CreateSongRequest
    ): CreateSongResponse

    @PUT("api/songs/{songId}")
    suspend fun updateSongRequest(
        @Path("songId") songId: String,
        @Body request: UpdateSongRequest
    ): SongDetailResponse

    @DELETE("api/songs/{id}")
    suspend fun deleteSongRequest(
        @Path("id") songId: String
    ): Response<Unit>

    @GET("api/songs/{songId}/categories")
    suspend fun getSongCategoriesRequest(
        @Path("songId") songId: String
    ): List<CategoryResponse>

    @POST("api/songs/{songId}/categories/{categoryId}")
    suspend fun addSongCategoryRequest(
        @Path("songId") songId: String,
        @Path("categoryId") categoryId: String
    ): Response<Unit>

    @DELETE("api/songs/{songId}/categories/{categoryId}")
    suspend fun deleteSongCategoryRequest(
        @Path("songId") songId: String,
        @Path("categoryId") categoryId: String
    ): Response<Unit>
}