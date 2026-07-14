using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Chord;

namespace ChordBook.Api.Tests.Chords;

public class PostChordEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;
    
    public PostChordEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }
    
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
    
    [Fact]
    public async Task PostChordBadRequestNameIsEmpty()
    {
        var client = _factory.CreateAuthenticatedClient();

        var request = new CreateChordRequest(
            "",
            "x02210"
        );

        var response = await client.PostAsJsonAsync("/api/chords", request);

        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
    }
    
    [Fact]
    public async Task PostChordBadRequestFingeringIsEmpty()
    {
        var client = _factory.CreateAuthenticatedClient();

        var request = new CreateChordRequest(
            "Am",
            ""
        );

        var response = await client.PostAsJsonAsync("/api/chords", request);

        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
    }
    
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