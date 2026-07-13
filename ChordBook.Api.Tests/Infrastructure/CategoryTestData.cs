using System.Net.Http.Json;
using ChordBook.DTO.Categories;

namespace ChordBook.Api.Tests.Infrastructure;

public static class CategoryTestData
{
    public static async Task<CategoryResponse> CreateCategoryAsync(
        HttpClient client,
        string name = "Test category")
    {
        var request = new CreateCategoryRequest(name);

        var response = await client.PostAsJsonAsync(
            "/api/categories",
            request);

        response.EnsureSuccessStatusCode();

        var category = await response.Content
            .ReadFromJsonAsync<CategoryResponse>();

        if (category is null)
        {
            throw new InvalidOperationException(
                "Created category response was empty.");
        }

        return category;
    }
}