using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.AspNetCore.TestHost;
using Microsoft.Extensions.DependencyInjection;
using ChordBook.Data;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Hosting;

namespace ChordBook.Api.Tests.Infrastructure;

/// <summary>
/// creates a test host for integration tests, configures test authentication and test environment variables.
/// </summary>
public sealed class CustomWebApplicationFactory
    : WebApplicationFactory<Program>
{
    /// <summary>
    /// Creates the test host and applies database migrations.
    /// </summary>
    protected override IHost CreateHost(IHostBuilder builder)
    {
        var host = base.CreateHost(builder);

        using var scope = host.Services.CreateScope();

        var dbContext = scope.ServiceProvider
            .GetRequiredService<ChordBookDbContext>();

        dbContext.Database.Migrate();

        return host;
    }
    public CustomWebApplicationFactory()
    {
        // JWT configuration used by integration tests
        Environment.SetEnvironmentVariable(
            "Jwt__Key",
            "ThisIsATestSecretKeyWithMoreThan32Characters");

        Environment.SetEnvironmentVariable(
            "Jwt__Issuer",
            "ChordBook.Api");
        
        // test authentication
        Environment.SetEnvironmentVariable(
            "Jwt__Audience",
            "ChordBook.Android");
        Environment.SetEnvironmentVariable(
            "Auth__Username",
            "test-user");

        Environment.SetEnvironmentVariable(
            "Auth__Password",
            "test-password");
    }

    /// <summary>
    /// loads the same database settings as the application
    /// </summary>
    /// <param name="builder"></param>
    protected override void ConfigureWebHost(IWebHostBuilder builder)
    {
        builder.UseEnvironment("Development");

        builder.ConfigureTestServices(services =>
        {
            services
                .AddAuthentication(options =>
                {
                    options.DefaultAuthenticateScheme =
                        TestAuthenticationHandler.SchemeName;

                    options.DefaultChallengeScheme =
                        TestAuthenticationHandler.SchemeName;
                })
                .AddScheme<
                    AuthenticationSchemeOptions,
                    TestAuthenticationHandler>(
                    TestAuthenticationHandler.SchemeName,
                    _ => { });
        });
    }
    /// <summary>
    /// creates authenticated HTTP client
    /// </summary>
    /// <returns></returns>
    public HttpClient CreateAuthenticatedClient()
    {
        var client = CreateClient();

        client.DefaultRequestHeaders.Authorization =
            new System.Net.Http.Headers.AuthenticationHeaderValue(
                TestAuthenticationHandler.SchemeName);

        return client;
    }
    
    /// <summary>
    /// creates an anonymous HTTP client
    /// </summary>
    public HttpClient CreateAnonymousClient()
    {
        return CreateClient();
    }
}