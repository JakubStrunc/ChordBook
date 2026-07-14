using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Chord;

namespace ChordBook.Api.Tests.Chords;


/// <summary>
/// integration tests for POST /api/chords
/// </summary>
public class PostChordEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;
    
    public PostChordEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }
    
    /// <summary>
    /// verifies that a new chord can be created
    /// </summary>
    [Fact]
    public async Task PostChordOk()
    {
        var client = _factory.CreateAuthenticatedClient();
        
        var request = new CreateChordRequest(
            "Am",
            "x02210"
        );
        
        var response = await client.PostAsJsonAsync("/api/chords", request);
        
        Assert.Equal(HttpStatusCode.Created, response.StatusCode);
    }
    
    /// <summary>
    /// verifies that creating a chord with an empty name returns 400
    /// </summary>
    [Fact]
    public async Task PostChordBadRequestWhenNameIsEmpty()
    {
        var client = _factory.CreateAuthenticatedClient();

        var request = new CreateChordRequest(
            "",
            "x02210"
        );

        var response = await client.PostAsJsonAsync("/api/chords", request);

        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
    }
    
    /// <summary>
    /// verifies that creating a chord with an empty fingering returns 400
    /// </summary>
    [Fact]
    public async Task PostChordBadRequestWhenFingeringIsEmpty()
    {
        var client = _factory.CreateAuthenticatedClient();

        var request = new CreateChordRequest(
            "Am",
            ""
        );

        var response = await client.PostAsJsonAsync("/api/chords", request);

        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
    }
    
    /// <summary>
    /// verifies that an anonymous user cannot create a chord
    /// </summary>
    [Fact]
    public async Task PostChordUnauthorized()
    {
        var client = _factory.CreateAnonymousClient();
        
        var request = new CreateChordRequest(
            "Am",
            "x02210"
        );
        
        var response = await client.PostAsJsonAsync("/api/chords", request);
        
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
    
}