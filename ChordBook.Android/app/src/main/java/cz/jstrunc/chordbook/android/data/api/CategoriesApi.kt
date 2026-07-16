package cz.jstrunc.chordbook.android.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

data class CategoryResponse(
    val id: String,
    val name: String
)

data class CreateCategoryRequest(
    val name: String
)

data class CreateCategoryResponse(
    val id: String
)

interface CategoriesApi {

    @GET("api/categories")
    suspend fun getCategoriesRequest(
    ): List<CategoryResponse>

    @POST("api/categories")
    suspend fun createCategoryRequest(
        @Body request: CreateCategoryRequest
    ): CreateCategoryResponse

//    @DELETE("api/categories")
//    suspend fun deleteCategory(
//        @Body request: DeleteCategoryRequest
//    ): CategoryResponse
}