using System.Net;
using System.Net.Http.Json;
using ChordBook.Api.Tests.Infrastructure;
using ChordBook.DTO.Auth;

namespace ChordBook.Api.Tests.Authorization;

public class LoginEndpointTests : IClassFixture<CustomWebApplicationFactory>
{
    private readonly CustomWebApplicationFactory _factory;

    public LoginEndpointTests(CustomWebApplicationFactory factory)
    {
        _factory = factory;
    }
    
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
    
    [Fact]
    public async Task LoginUnauthorized_WhenCredentialsAreInvalid()
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