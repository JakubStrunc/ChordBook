using System.Net;
using ChordBook.Api.Tests.Infrastructure;

namespace ChordBook.Api.Tests.Chords;


/// <summary>
/// integration tests for DELETE /api/chords/{id}
/// </summary>
public class DeleteChordEndpointTests
    : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;

    public DeleteChordEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    
    /// <summary>
    /// verifies that an existing chord can be deleted
    /// </summary>
    [Fact]
    public async Task DeleteChordOk()
    {
        var client = _factory.CreateAuthenticatedClient();

        var chord = await ChordTestData.CreateChordAsync(
            client,
            $"Am-{Guid.NewGuid():N}"[..10]
            );

        var response = await client.DeleteAsync(
            $"/api/chords/{chord.Id}");

        Assert.Equal(HttpStatusCode.NoContent, response.StatusCode);
    }

    /// <summary>
    /// verifies that deleting a non-existing chord returns 404
    /// </summary>
    [Fact]
    public async Task DeleteChordNotFound()
    {
        var client = _factory.CreateAuthenticatedClient();

        var response = await client.DeleteAsync(
            $"/api/chords/{Guid.NewGuid()}");

        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }

    /// <summary>
    /// verifies that an anonymous user cannot delete a chord
    /// </summary>
    [Fact]
    public async Task DeleteChordUnauthorized()
    {
        var client = _factory.CreateAnonymousClient();

        var response = await client.DeleteAsync(
            $"/api/chords/{Guid.NewGuid()}");

        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
}