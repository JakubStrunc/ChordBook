using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.AspNetCore.TestHost;
using Microsoft.Extensions.DependencyInjection;

namespace ChordBook.Api.Tests.Infrastructure;


public sealed class CustomWebApplicationFactory
    : WebApplicationFactory<Program>
{
    public CustomWebApplicationFactory()
    {
        Environment.SetEnvironmentVariable(
            "Jwt__Key",
            "ThisIsATestSecretKeyWithMoreThan32Characters");

        Environment.SetEnvironmentVariable(
            "Jwt__Issuer",
            "ChordBook.Api");

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

    public HttpClient CreateAuthenticatedClient()
    {
        var client = CreateClient();

        client.DefaultRequestHeaders.Authorization =
            new System.Net.Http.Headers.AuthenticationHeaderValue(
                TestAuthenticationHandler.SchemeName);

        return client;
    }

    public HttpClient CreateAnonymousClient()
    {
        return CreateClient();
    }
}