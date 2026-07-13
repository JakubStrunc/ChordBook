using ChordBook.Services;

namespace ChordBook.Endpoints;

public static class SongEndpoints
{
    public static IEndpointRouteBuilder MapSongEndpoints(
        this IEndpointRouteBuilder endpoints)
    {
        endpoints.MapGet("/api/songs", async (
                SongService songService,
                CancellationToken cancellationToken) =>
            {

                var songs = await songService.GetSongsAsync(cancellationToken);

                return Results.Ok(songs);

            })
            .RequireAuthorization();
        
        return endpoints;
    }
}