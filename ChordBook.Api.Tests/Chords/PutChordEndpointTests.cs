using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Chord;

namespace ChordBook.Api.Tests.Chords;


/// <summary>
/// integration tests for PUT /api/chords/{id}
/// </summary>
public class PutChordEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;

    public PutChordEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    /// <summary>
    /// verifies that an existing chord can be updated
    /// </summary>
    [Fact]
    public async Task PutChordOk()
    {
        var client = _factory.CreateAuthenticatedClient();

        var chord = await ChordTestData.CreateChordAsync(client);

        var request = new UpdateChordRequest(
            "Am7",
            "x02010");

        var response = await client.PutAsJsonAsync(
            $"/api/chords/{chord.Id}",
            request);

        Assert.Equal(HttpStatusCode.OK, response.StatusCode);

        var updatedChord = await response.Content
            .ReadFromJsonAsync<ChordResponse>();

        Assert.NotNull(updatedChord);
        Assert.Equal(chord.Id, updatedChord.Id);
        Assert.Equal(request.Name, updatedChord.Name);
        Assert.Equal(request.Fingering, updatedChord.Fingering);
    }

    /// <summary>
    /// verifies that updating a non-existing chord returns 404
    /// </summary>
    [Fact]
    public async Task PutChordNotFound()
    {
        var client = _factory.CreateAuthenticatedClient();

        var request = new UpdateChordRequest(
            "Am7",
            "x02010");

        var response = await client.PutAsJsonAsync(
            $"/api/chords/{Guid.NewGuid()}",
            request);

        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }

    /// <summary>
    /// verifies that an anonymous user cannot update a chord
    /// </summary>
    [Fact]
    public async Task PutChordUnauthorized()
    {
        var client = _factory.CreateAnonymousClient();

        var request = new UpdateChordRequest(
            "Am7",
            "x02010");

        var response = await client.PutAsJsonAsync(
            $"/api/chords/{Guid.NewGuid()}",
            request);

        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
}