using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Chord;

namespace ChordBook.Api.Tests.Chords;


/// <summary>
/// integration tests for GET /api/chords/{id}
/// </summary>
[Collection("IntegrationTests")]
public class GetChordEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;

    public GetChordEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    
    /// <summary>
    /// verifies that an existing chord is returned
    /// </summary>
    [Fact]
    public async Task GetChordOk()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var createdChord = await ChordTestData.CreateChordAsync(
            client,
            $"Am-{Guid.NewGuid():N}"[..10]
            );
        
        var response = await client.GetAsync($"/api/chords/{createdChord.Id}");
        
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        
        var chord = await response.Content
            .ReadFromJsonAsync<ChordResponse>();
        
        Assert.NotNull(chord);
        Assert.Equal(createdChord.Id, chord.Id);
        Assert.NotNull(chord.Name);
        Assert.NotNull(chord.Fingering);
    }
    
    /// <summary>
    /// verifies that requesting a non-existing chord returns 404
    /// </summary>
    [Fact]
    public async Task GetChordNotFound()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var response = await client.GetAsync($"/api/chords/{Guid.NewGuid()}");
        
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }
    
    /// <summary>
    /// verifies that an anonymous user cannot access the endpoint
    /// </summary>
    [Fact]
    public async Task GetChordUnauthenticated()
    {
        var client = _factory.CreateAnonymousClient();
        
        var response = await client.GetAsync($"/api/chords/{Guid.NewGuid()}");
        
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
}