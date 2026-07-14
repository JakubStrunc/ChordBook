using System.Security.Claims;
using System.Text.Encodings.Web;
using Microsoft.AspNetCore.Authentication;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;

namespace ChordBook.Api.Tests.Infrastructure;


/// <summary>
/// authentication handler
/// </summary>
public sealed class TestAuthenticationHandler
    : AuthenticationHandler<AuthenticationSchemeOptions>
{
    public const string SchemeName = "Test";
    
    
    /// <summary>
    /// authentication scheme used by test clients
    /// </summary>
    public TestAuthenticationHandler(
        IOptionsMonitor<AuthenticationSchemeOptions> options,
        ILoggerFactory logger,
        UrlEncoder encoder)
        : base(options, logger, encoder)
    {
    }

    /// <summary>
    /// authenticates requests
    /// </summary>
    protected override Task<AuthenticateResult> HandleAuthenticateAsync()
    {
        
        var authorizationHeader = Request.Headers.Authorization.ToString();

        // if no authorization header then anonymous request
        if (string.IsNullOrWhiteSpace(authorizationHeader))
        {
            return Task.FromResult(AuthenticateResult.NoResult());
        }
        
        // different authentication scheme then reject requests
        if (!authorizationHeader.StartsWith(
                SchemeName,
                StringComparison.OrdinalIgnoreCase))
        {
            return Task.FromResult(
                AuthenticateResult.Fail("Invalid authentication scheme."));
        }
        
        // create a test identity
        var claims = new[]
        {
            new Claim(ClaimTypes.NameIdentifier, "test-user-id"),
            new Claim(ClaimTypes.Name, "Test User")
        };

        var identity = new ClaimsIdentity(claims, SchemeName);
        var principal = new ClaimsPrincipal(identity);
        var ticket = new AuthenticationTicket(principal, SchemeName);

        return Task.FromResult(
            AuthenticateResult.Success(ticket));
    }
}