using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Songs;
using Microsoft.AspNetCore.Mvc.Testing;

namespace ChordBook.Api.Tests.Songs;

public class PutSongEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;
    
    public PutSongEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

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