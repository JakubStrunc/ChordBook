using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using ChordBook.DTO.Auth;
using Microsoft.IdentityModel.Tokens;

namespace ChordBook.Services;

/// <summary>
/// Provides authentication and JWT token generation.
/// </summary>
public class AuthService(IConfiguration configuration)
{
    /// <summary>
    /// Validates login credentials and returns a JWT access token.
    /// </summary>
    public LoginResponse? Login(LoginRequest request)
    {
        var configuredUsername = configuration["Auth:Username"];
        var configuredPassword = configuration["Auth:Password"];

        if (request.Username != configuredUsername ||
            request.Password != configuredPassword)
        {
            return null;
        }

        var jwtKey = configuration["Jwt:Key"]
                     ?? throw new InvalidOperationException(
                         "JWT key is missing.");

        var jwtIssuer = configuration["Jwt:Issuer"]
                        ?? throw new InvalidOperationException(
                            "JWT issuer is missing.");

        var jwtAudience = configuration["Jwt:Audience"]
                          ?? throw new InvalidOperationException(
                              "JWT audience is missing.");

        var claims = new[]
        {
            new Claim(
                ClaimTypes.Name,
                request.Username)
        };

        var signingKey = new SymmetricSecurityKey(
            Encoding.UTF8.GetBytes(jwtKey));

        var credentials = new SigningCredentials(
            signingKey,
            SecurityAlgorithms.HmacSha256);

        var token = new JwtSecurityToken(
            issuer: jwtIssuer,
            audience: jwtAudience,
            claims: claims,
            expires: DateTime.UtcNow.AddHours(24),
            signingCredentials: credentials);

        var accessToken = new JwtSecurityTokenHandler()
            .WriteToken(token);

        return new LoginResponse(accessToken);
    }
}