using ChordBook.DTO.Songs;
using ChordBook.Services;
using Superpower.Model;

namespace ChordBook.Endpoints;


/// <summary>
/// song API endpoints
/// </summary>
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
        
        endpoints.MapGet("/api/songs/{songId:guid}", async (
                Guid songId,
                SongService songService,
                CancellationToken cancellationToken) =>
            {

                var song = await songService.GetSongAsync(songId, cancellationToken);

                return  song == null ? Results.NotFound() : Results.Ok(song);

            })
            .RequireAuthorization();
        
        endpoints.MapPost("/api/songs", async (
                CreateSongRequest request,
                SongService songService,
                CancellationToken cancellationToken) =>
            {

                var songId = await songService.CreateSongAsync(request, cancellationToken);

                return  Results.Created(
                    $"/api/songs/{songId}",
                    new {id = songId});

            })
            .RequireAuthorization();
        
        endpoints.MapPut("/api/songs/{songId:guid}", async (
            Guid songId,
            UpdateSongRequest updateSongRequest,
            SongService songService,
            CancellationToken cancellationToken) =>
        {
            var song = await songService.UpdateSongAsync(songId, updateSongRequest, cancellationToken);
            
            return song == null ? Results.NotFound() : Results.Ok(song) ;
        })
        .RequireAuthorization();
        
        endpoints.MapDelete("/api/songs/{songId:guid}", async (
                Guid songId,
                SongService songService,
                CancellationToken cancellationToken) =>
            {

                var isDeleted = await songService.DeleteSongAsync(songId, cancellationToken);

                return isDeleted ? Results.NoContent() : Results.NotFound();

            })
            .RequireAuthorization();
        

        return endpoints;
    }
}