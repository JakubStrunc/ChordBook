using ChordBook.Data;

namespace ChordBook.Endpoints;


/// <summary>
/// health check endpoints
/// </summary>
public static class HealthEndpoints
{
    public static IEndpointRouteBuilder MapHealthEndpoints(
        this IEndpointRouteBuilder endpoints)
    {
        endpoints.MapGet("/health", () =>
            Results.Ok(new
            {
                status = "Healthy",
                timestamp = DateTimeOffset.UtcNow
            }));

        endpoints.MapGet("/health/database", async (
                ChordBookDbContext dbContext,
                CancellationToken cancellationToken) =>
            {
                var canConnect = await dbContext.Database.CanConnectAsync(cancellationToken);

                return canConnect
                    ? Results.Ok(
                        new
                        {
                            status = "Healthy",
                            database = "Connected"
                        })
                    : Results.Problem(
                        statusCode: StatusCodes.Status503ServiceUnavailable,
                        title: "Database unavailable");
            }
        );

        return endpoints;
    }
}