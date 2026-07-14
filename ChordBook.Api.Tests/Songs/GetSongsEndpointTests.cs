using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Songs;

namespace ChordBook.Api.Tests.Songs;

/// <summary>
/// integration tests for GET /api/songs
/// </summary>
[Collection("IntegrationTests")]
public class GetSongsEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;

    public GetSongsEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    /// <summary>
    /// verifies that all songs are returned
    /// </summary>
    [Fact]
    public async Task GetSongsOk()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        // call the endpoint
        var response = await client.GetAsync("/api/songs");
        
        // verify that endpoint returned 200
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        
        var songs = await response.Content
            .ReadFromJsonAsync<List<SongListItemResponse>>();
        
        // verify that body is not null
        Assert.NotNull(songs);
    }
    
    /// <summary>
    /// verifies that an anonymous user cannot access the endpoint
    /// </summary>
    [Fact]
    public async Task GetSongsUnauthorized()
    {
        // Arrange
        var client = _factory.CreateAnonymousClient();

        // Act
        var response = await client.GetAsync("/api/songs");

        // Assert
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
}