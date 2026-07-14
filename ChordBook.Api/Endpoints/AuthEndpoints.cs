using ChordBook.DTO.Auth;
using ChordBook.Services;

namespace ChordBook.Endpoints;

/// <summary>
/// Maps authentication related API endpoints.
/// </summary>
public static class AuthEndpoints
{
    public static IEndpointRouteBuilder MapAuthEndpoints(
        this IEndpointRouteBuilder endpoints)
    {
        endpoints.MapPost("/api/auth/login", (
            LoginRequest request,
            AuthService authService) =>
        {
            if (string.IsNullOrWhiteSpace(request.Username) ||
                string.IsNullOrWhiteSpace(request.Password))
            {
                return Results.BadRequest();
            }

            var response = authService.Login(request);

            return response is null
                ? Results.Unauthorized()
                : Results.Ok(response);
        });

        return endpoints;
    }
}