using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Songs;

namespace ChordBook.Api.Tests.Songs;


/// <summary>
/// integration tests for POST /api/songs
/// </summary>
public class PostSongEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    
    private readonly CustomWebApplicationFactory _factory;

    public PostSongEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }
    
    /// <summary>
    /// verifies that a new song can be created
    /// </summary>
    [Fact]
    public async Task PostSongOk()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var request = new CreateSongRequest(
            "Wonderwall",
            "Oasis"
            );
        
        var response = await client.PostAsJsonAsync("/api/songs", request);
        
        Assert.Equal(HttpStatusCode.Created, response.StatusCode);
    }
    
    
    /// <summary>
    /// verifies that an anonymous user cannot create a song
    /// </summary>
    [Fact]
    public async Task PostSongUnauthorized()
    {
        var client = _factory.CreateAnonymousClient();
        
        var request = new CreateSongRequest(
            $"Wonderwall-{Guid.NewGuid()}",
            "Oasis"
        );
        
        var response = await client.PostAsJsonAsync("/api/songs", request);
        
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
    
}