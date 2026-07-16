package cz.jstrunc.chordbook.android.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * request for updating a chord position in a song line
 */
data class UpdateChordPositionRequest(
    val characterIndex: Int,
    val chordId: String
)

/**
 * request for creating a new chord
 */
data class CreateChordRequest(
    val name: String,
    val fingering: String
)

/**
 * chord returned by the API
 */
data class ChordResponse(
    val id: String,
    val name: String,
    val fingering: String
)

/**
 * chord API endpoints
 */
interface ChordsApi {

    @GET("api/chords")
    suspend fun getChordsRequest(): List<ChordResponse>

    @POST("api/chords")
    suspend fun createChordRequest(
        @Body request: CreateChordRequest
    ): ChordResponse

    @DELETE("api/chords/{chordId}")
    suspend fun deleteChordRequest(
        @Path("chordId") chordId: String
    )
}