using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Auth;

namespace ChordBook.Api.Tests.Authorization;


/// <summary>
/// integration tests for POST /api/auth/login
/// </summary>
[Collection("IntegrationTests")]
public class LoginEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;

    public LoginEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }
    
    
    /// <summary>
    /// verifies that a valid user can log in and receives JWT
    /// </summary>
    [Fact]
    public async Task LoginOk()
    {
        var client = _factory.CreateAnonymousClient();

        var request = new LoginRequest(
            "test-user",
            "test-password");

        var response = await client.PostAsJsonAsync(
            "/api/auth/login",
            request);

        Assert.Equal(HttpStatusCode.OK, response.StatusCode);

        var result = await response.Content
            .ReadFromJsonAsync<LoginResponse>();

        Assert.NotNull(result);
        Assert.False(string.IsNullOrWhiteSpace(result.AccessToken));
    }
    
    /// <summary>
    /// verifies that invalid credentials return 401 Unauthorized
    /// </summary>
    [Fact]
    public async Task LoginUnauthorizedCredentialsAreInvalid()
    {
        var client = _factory.CreateAnonymousClient();

        var request = new LoginRequest(
            "wrong-user",
            "wrong-password");

        var response = await client.PostAsJsonAsync(
            "/api/auth/login",
            request);

        Assert.Equal(
            HttpStatusCode.Unauthorized,
            response.StatusCode);
    }
}