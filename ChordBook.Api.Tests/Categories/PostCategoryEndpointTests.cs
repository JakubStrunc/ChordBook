using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Categories;

namespace ChordBook.Api.Tests.Categories;


/// <summary>
/// integration tests for POST /api/categories
/// </summary>
public class PostCategoryEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;
    
    public PostCategoryEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    
    /// <summary>
    /// verifies that a new category can be created
    /// </summary>
    [Fact]
    public async Task PostCategoryOk()
    {
        var client = _factory.CreateAuthenticatedClient();

        var category = new CreateCategoryRequest(
            $"Camp-{Guid.NewGuid()}"
            );
        
        var response = await client.PostAsJsonAsync("/api/categories", category);
        
        var responseBody = await response.Content.ReadAsStringAsync();
        
        Assert.True(
            response.IsSuccessStatusCode,
            $"Status: {response.StatusCode}, Body: {responseBody}");
    }
    
    /// <summary>
    /// verifies that an anonymous user cannot create a category
    /// </summary>
    [Fact]
    public async Task PostCategoryUnauthorized()
    {
        var client = _factory.CreateAnonymousClient();
        
        var category = new CreateCategoryRequest(
            "Camp"
        );
        
        var response = await client.PostAsJsonAsync("/api/categories", category);
        
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
}