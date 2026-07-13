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

        return endpoints;
    }
}