using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;

namespace ChordBook.Api.Tests.Categories;

public class GetCategoriesEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;

    public GetCategoriesEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    [Fact]
    public async Task GetCategoriesOk()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var response = await client.GetAsync("/api/categories");
        
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        
        var categories = await response.Content.ReadFromJsonAsync<List<string>>();
        
        Assert.NotNull(categories);
    }
    
    [Fact]
    public async Task GetCategoriesUnauthorized()
    {
        var client = _factory.CreateAnonymousClient();
        
        var response = await client.GetAsync("/api/categories");
        
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
}