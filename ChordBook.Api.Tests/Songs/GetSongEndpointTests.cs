using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Songs;

namespace ChordBook.Api.Tests.Songs;



/// <summary>
/// integration tests for GET /api/songs/{id}
/// </summary>
[Collection("IntegrationTests")]
public class GetSongEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;

    public GetSongEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    
    /// <summary>
    /// verifies that an existing song is returned
    /// </summary>
    [Fact]
    public async Task GetSongOk()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var createdSong = await SongTestData.CreateSongAsync(client);

        var response = await client.GetAsync($"api/songs/{createdSong.Id}");
        
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);

        var song = await response.Content
            .ReadFromJsonAsync<SongDetailResponse>();
        
        Assert.NotNull(song);
        Assert.Equal(createdSong.Id, song.Id);
        Assert.NotNull(song.Categories);
        Assert.NotNull(song.Lines);
        
    }

    
    /// <summary>
    /// verifies that requesting a non-existing song returns 404
    /// </summary>
    [Fact]
    public async Task GetSongNotFound()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var response = await client.GetAsync($"api/songs/{Guid.NewGuid()}");
        
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }
    
    /// <summary>
    /// verifies that an anonymous user cannot access the endpoint
    /// </summary>
    [Fact]
    public async Task GetSongUnauthorized()
    {
        var client = _factory.CreateAnonymousClient();
        
        var response = await client.GetAsync($"api/songs/{Guid.NewGuid()}");

        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
    
}