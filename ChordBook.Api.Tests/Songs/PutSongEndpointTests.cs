using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Songs;

namespace ChordBook.Api.Tests.Songs;


/// <summary>
/// integration tests for PUT /api/songs/{id}
/// </summary>
[Collection("IntegrationTests")]
public class PutSongEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;
    
    public PutSongEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    
    /// <summary>
    /// verifies that an existing song can be updated
    /// </summary>
    [Fact]
    public async Task PutSongOk()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var createdSong = await SongTestData.CreateSongAsync(client);

        
        var request = new UpdateSongRequest(
            "Updated title",
            "Updated artist");
        
        var response = await client.PutAsJsonAsync(
            $"/api/songs/{createdSong.Id}",
            request);
        
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);

        var song = await response.Content
            .ReadFromJsonAsync<SongDetailResponse>();

        Assert.NotNull(song);
        Assert.Equal(createdSong.Id, song.Id);
        Assert.Equal(request.Title, song.Title);
        Assert.Equal(request.Artist, song.Artist);
    }
    
    /// <summary>
    /// verifies that updating a non-existing song returns 404
    /// </summary>
    [Fact]
    public async Task PutSongNotFound()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var songId = Guid.NewGuid();
        
        var request = new UpdateSongRequest(
            "Updated title",
            "Updated artist");
        
        var response = await client.PutAsJsonAsync(
            $"/api/songs/{songId}",
            request);
        
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
        
    }
    /// <summary>
    /// verifies that an anonymous user cannot update a song
    /// </summary>
    [Fact]
    public async Task PutSongUnauthorized()
    {
        var client = _factory.CreateAnonymousClient();
        
        var songId = Guid.NewGuid();
        
        var request = new UpdateSongRequest(
            "Updated title",
            "Updated artist");
        
        var response = await client.PutAsJsonAsync(
            $"/api/songs/{songId}",
            request);
        
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
        
    }
    
}