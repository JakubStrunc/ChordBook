using System.Net;
using ChordBook.Api.Tests.Infrastructure;

namespace ChordBook.Api.Tests.Songs;

public class DeleteSongEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;

    public DeleteSongEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }
    
    [Fact]
    public async Task DeleteSongOk()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var createdSong = await SongTestData.CreateSongAsync(client);

        var response = await client.DeleteAsync(
            $"/api/songs/{createdSong.Id}");

        Assert.Equal(HttpStatusCode.NoContent, response.StatusCode);
    }
    
    [Fact]
    public async Task DeleteSongNotFound()
    {
        var client = _factory.CreateAuthenticatedClient();

        var songId = Guid.NewGuid();

        var response = await client.DeleteAsync(
            $"/api/songs/{songId}");

        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }
    
    [Fact]
    public async Task DeleteSongUnauthorized()
    {
        var client = _factory.CreateAnonymousClient();

        var songId = Guid.NewGuid();

        var response = await client.DeleteAsync(
            $"/api/songs/{songId}");

        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
    
    
}