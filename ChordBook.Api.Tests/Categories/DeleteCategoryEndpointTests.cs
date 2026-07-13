using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Categories;

namespace ChordBook.Api.Tests.Categories;

public class DeleteCategoryEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;
    
    public DeleteCategoryEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    [Fact]
    public async Task DeleteCategoryOk()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var createdCategory = await CategoryTestData.CreateCategoryAsync(client);
        
        var responce =  await client.DeleteAsync(
            $"/api/songs/{createdCategory.Id}");
        
        Assert.Equal(HttpStatusCode.NoContent, responce.StatusCode);
    }

    [Fact]
    public async Task DeleteCategoryNotFound()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var songId = Guid.NewGuid();
        
        var responce = await client.DeleteAsync($"/api/songs/{songId}");
        
        Assert.Equal(HttpStatusCode.NotFound, responce.StatusCode);
    }
    
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