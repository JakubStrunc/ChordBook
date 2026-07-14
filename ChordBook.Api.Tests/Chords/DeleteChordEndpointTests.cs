using System.Net;
using ChordBook.Api.Tests.Infrastructure;

namespace ChordBook.Api.Tests.Chords;

public class DeleteChordEndpointTests
    : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;

    public DeleteChordEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }

    [Fact]
    public async Task DeleteChordOk()
    {
        var client = _factory.CreateAuthenticatedClient();

        var chord = await ChordTestData.CreateChordAsync(client);

        var response = await client.DeleteAsync(
            $"/api/chords/{chord.Id}");

        Assert.Equal(HttpStatusCode.NoContent, response.StatusCode);
    }

    [Fact]
    public async Task DeleteChordNotFound()
    {
        var client = _factory.CreateAuthenticatedClient();

        var response = await client.DeleteAsync(
            $"/api/chords/{Guid.NewGuid()}");

        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }

    [Fact]
    public async Task DeleteChordUnauthorized()
    {
        var client = _factory.CreateAnonymousClient();

        var response = await client.DeleteAsync(
            $"/api/chords/{Guid.NewGuid()}");

        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }
}