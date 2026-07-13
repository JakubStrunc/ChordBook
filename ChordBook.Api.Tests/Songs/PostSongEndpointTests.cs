using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Songs;

namespace ChordBook.Api.Tests.Songs;

public class PostSongEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    
    private readonly CustomWebApplicationFactory _factory;

    public PostSongEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    [Fact]
    public async Task PostSongOk()
    {
        var client = _factory.CreateClient();
        
        var request = new CreateSongRequest(
            "Wonderwall",
            "Oasis"
            );
        
        var response = await client.PostAsJsonAsync("/api/songs", request);
        
        Assert.Equal(HttpStatusCode.Created, response.StatusCode);
    }
    
    
    [Fact]
    public async Task PostSongUnauthorized()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var request = new CreateSongRequest(
            "Wonderwall",
            "Oasis"
        );
        
        var response = await client.PostAsJsonAsync("/api/songs", request);
        
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
    
}