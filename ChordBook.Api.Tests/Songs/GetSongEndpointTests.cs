using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Songs;

namespace ChordBook.Api.Tests.Songs;

public class GetSongEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;

    public GetSongEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    [Fact]
    public async Task GetSongOk()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var createdSong = await SongTestData.CreateSongAsync(client);

        var response = await client.GetAsync($"api/songs/{createdSong.Id}");
        
        // verify that endpoint returned 200
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);

        var song = await response.Content
            .ReadFromJsonAsync<SongDetailResponse>();
        
        Assert.NotNull(song);
        Assert.Equal(createdSong.Id, song.Id);
        Assert.NotNull(song.Categories);
        Assert.NotNull(song.Lines);
        
    }

    [Fact]
    public async Task GetSongNotFound()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var songId = Guid.NewGuid();
        
        var response = await client.GetAsync($"api/songs/{songId}");
        
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }
    
    [Fact]
    public async Task GetSongUnauthorized()
    {
        var client = _factory.CreateAnonymousClient();
        
        // call the endpoint
        var songId = Guid.Parse("1");
        var response = await client.GetAsync($"api/songs/{songId}");

        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
    
}