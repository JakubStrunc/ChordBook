using ChordBook.DTO.Categories;
using ChordBook.Services;

namespace ChordBook.Endpoints;

/// <summary>
/// category API endpoints
/// </summary>
public static class CategoryEndpoints
{

    public static IEndpointRouteBuilder MapCategoryEndpoints(
        this IEndpointRouteBuilder endpoints)
    {


        endpoints.MapGet("/api/categories", async (
                CategoryService categoryService,
                CancellationToken cancellationToken) =>
            {

                var categories = await categoryService.GetCategoriesAsync(cancellationToken);

                return Results.Ok(categories);

            })
            .RequireAuthorization();

        endpoints.MapPost("/api/categories", async (
            CreateCategoryRequest createCategoryRequest,
            CategoryService categoryService,
            CancellationToken cancellationToken) =>
        {

            var categoryId = await categoryService.CreateCategoryAsync(createCategoryRequest, cancellationToken);

            return Results.Created($"/api/categories/{categoryId}", new { id = categoryId });

        })
        .RequireAuthorization();

        endpoints.MapDelete("/api/categories/{categoryId:guid}", async (
                Guid categoryId,
                CategoryService categoryService,
                CancellationToken cancellationToken) =>
            {
                var isDeleted = await categoryService.DeleteCategoryAsync(categoryId, cancellationToken);

                return isDeleted ? Results.NoContent() : Results.NotFound();

            })
            .RequireAuthorization();
        
        
        return endpoints;
    }
}