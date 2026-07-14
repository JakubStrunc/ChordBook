using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Categories;

namespace ChordBook.Api.Tests.Categories;


/// <summary>
/// integration tests for GET /api/categories
/// </summary>
public class GetCategoriesEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;

    public GetCategoriesEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    
    /// <summary>
    /// verifies that all categories are returned
    /// </summary>
    [Fact]
    public async Task GetCategoriesOk()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var response = await client.GetAsync("/api/categories");
        
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        
        var categories = await response.Content.ReadFromJsonAsync<List<CategoryResponse>>();
        
        Assert.NotNull(categories);
    }
    
    /// <summary>
    /// verifies that an anonymous user cannot access the endpoint
    /// </summary>
    [Fact]
    public async Task GetCategoriesUnauthorized()
    {
        var client = _factory.CreateAnonymousClient();
        
        var response = await client.GetAsync("/api/categories");
        
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
}