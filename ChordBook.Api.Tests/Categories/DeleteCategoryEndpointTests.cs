using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Categories;

namespace ChordBook.Api.Tests.Categories;


/// <summary>
/// integration tests for DELETE /api/categories/{id}
/// </summary>
[Collection("IntegrationTests")]
public class DeleteCategoryEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;
    
    public DeleteCategoryEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    /// <summary>
    /// verifies that an existing category can be deleted
    /// </summary>
    [Fact]
    public async Task DeleteCategoryOk()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var createdCategory = await CategoryTestData.CreateCategoryAsync(client, $"test-category-{Guid.NewGuid()}");
        
        var response =  await client.DeleteAsync(
            $"/api/categories/{createdCategory.Id}");
        
        Assert.Equal(HttpStatusCode.NoContent, response.StatusCode);
    }

    /// <summary>
    /// verifies that deleting a non-existing category returns 404
    /// </summary>
    [Fact]
    public async Task DeleteCategoryNotFound()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var songId = Guid.NewGuid();
        
        var response = await client.DeleteAsync($"/api/categories/{songId}");
        
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }
    
    /// <summary>
    /// verifies that an anonymous user cannot delete a category
    /// </summary>
    [Fact]
    public async Task DeleteCategoryUnauthorized()
    {
        var client = _factory.CreateAnonymousClient();

        var categoryId = Guid.NewGuid();

        var response = await client.DeleteAsync(
            $"/api/categories/{categoryId}");

        Assert.Equal(
            HttpStatusCode.Unauthorized,
            response.StatusCode);
    }
}