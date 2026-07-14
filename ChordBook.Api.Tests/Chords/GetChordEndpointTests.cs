using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Chord;

namespace ChordBook.Api.Tests.Chords;

public class GetChordEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;

    public GetChordEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    [Fact]
    public async Task GetChordOk()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var createdChord = await ChordTestData.CreateChordAsync(client);
        
        var response = await client.GetAsync($"/api/chords/{createdChord.Id}");
        
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        
        var chord = await response.Content
            .ReadFromJsonAsync<ChordResponse>();
        
        Assert.NotNull(chord);
        Assert.Equal(createdChord.Id, chord.Id);
        Assert.NotNull(chord.Name);
        Assert.NotNull(chord.Fingering);
    }
    
    [Fact]
    public async Task GetChordNotFound()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var response = await client.GetAsync($"/api/chords/{Guid.NewGuid()}");
        
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }
    
    [Fact]
    public async Task GetChordUnauthenticated()
    {
        var client = _factory.CreateAnonymousClient();
        
        var response = await client.GetAsync($"/api/chords/{Guid.NewGuid()}");
        
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
}