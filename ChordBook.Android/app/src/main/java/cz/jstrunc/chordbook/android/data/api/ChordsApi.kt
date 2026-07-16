package cz.jstrunc.chordbook.android.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


data class UpdateChordPositionRequest(
    val characterIndex: Int,
    val chordId: String
)

data class CreateChordRequest(
    val name: String,
    val fingering: String
)

data class ChordResponse(
    val id: String,
    val name: String,
    val fingering: String
)

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