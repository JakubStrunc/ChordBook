using System.Net;
using ChordBook.Api.Tests.Infrastructure;

namespace ChordBook.Api.Tests.Health;


/// <summary>
/// integration tests for health check endpoints
/// </summary>
public class HealthTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly HttpClient _client;

    public HealthTests(CustomWebApplicationFactory factory)
    {
        _client = factory.CreateClient();
    }
    
    /// <summary>
    /// verifies that the application health endpoint is available
    /// </summary>
    [Fact]
    public async Task GetHealth_ReturnsOk()
    {
        var response = await _client.GetAsync("/health");

        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
    }
    
    /// <summary>
    /// verifies that the database health endpoint is available
    /// </summary>
    [Fact]
    public async Task GetDatabaseHealth_ReturnsOk()
    {
        var response = await _client.GetAsync("/health/database");

        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
    }
}