package cz.jstrunc.chordbook.android.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * category returned by the API.
 */
data class CategoryResponse(
    val id: String,
    val name: String
)

/**
 * request for creating a new category
 */
data class CreateCategoryRequest(
    val name: String
)

/**
 * response returned after creating a category
 */
data class CreateCategoryResponse(
    val id: String
)


/**
 * category API endpoints
 */
interface CategoriesApi {

    @GET("api/categories")
    suspend fun getCategoriesRequest(
    ): List<CategoryResponse>

    @POST("api/categories")
    suspend fun createCategoryRequest(
        @Body request: CreateCategoryRequest
    ): CreateCategoryResponse
}