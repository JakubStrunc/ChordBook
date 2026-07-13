using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Categories;

namespace ChordBook.Api.Tests.Categories;

public class PostCategoryEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;
    
    public PostCategoryEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    [Fact]
    public async Task PostCategoryOk()
    {
        var client = _factory.CreateAuthenticatedClient();

        var category = new CreateCategoryRequest(
            "Camp"
            );
        
        var response = await client.PostAsJsonAsync("/api/categories", category);
        
        Assert.Equal(HttpStatusCode.Created, response.StatusCode);
    }
    
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