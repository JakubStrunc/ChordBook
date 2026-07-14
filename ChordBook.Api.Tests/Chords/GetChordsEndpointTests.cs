using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Chord;

namespace ChordBook.Api.Tests.Chords;

/// <summary>
/// integration tests for GET /api/chords
/// </summary>
[Collection("IntegrationTests")]
public class GetChordsEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    
    private readonly CustomWebApplicationFactory _factory;
    
    public GetChordsEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }
    
    /// <summary>
    /// verifies that all supported chords are returned
    /// </summary>
    [Fact]
    public async Task GetChordsOk()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var response = await client.GetAsync("/api/chords");
        
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        
        var chords = await response.Content
            .ReadFromJsonAsync<List<ChordResponse>>();
        
        Assert.NotNull(chords);
    }
    
    /// <summary>
    /// verifies that an anonymous user cannot access the endpoint
    /// </summary>
    [Fact]
    public async Task GetChordsUnauthorized()
    {
        var client = _factory.CreateAnonymousClient();
        
        var response = await client.GetAsync("/api/chords");
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
    
}