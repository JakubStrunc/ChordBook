using ChordBook.DTO.Chord;
using ChordBook.Services;
using ChordBook.Services.Results;

namespace ChordBook.Endpoints;

public static class ChordEndpoints
{
    public static IEndpointRouteBuilder MapChordEndpoints(
        this IEndpointRouteBuilder endpoints)
    {
        endpoints.MapGet("/api/chords", async (
                ChordService chordService,
                CancellationToken cancellationToken) =>
            {
                var chords = await chordService.GetChordsAsync(
                    cancellationToken);

                return Results.Ok(chords);
            })
            .RequireAuthorization();
        
        endpoints.MapGet("/api/chords/{chordId:guid}", async (
                Guid chordId,
                ChordService chordService,
                CancellationToken cancellationToken) =>
            {
                var chord = await chordService.GetChordAsync(
                    chordId,
                    cancellationToken);

                return chord == null
                    ? Results.NotFound()
                    : Results.Ok(chord);
            })
            .RequireAuthorization();
        
        endpoints.MapPost("/api/chords", async (
                CreateChordRequest request,
                ChordService chordService,
                CancellationToken cancellationToken) =>
            {
                
                var chordId = await chordService.CreateChordAsync(
                    request,
                    cancellationToken);

                return chordId == null
                    ? Results.BadRequest()
                    : Results.Created(
                        $"/api/chords/{chordId}",
                        new { id = chordId });
            })
            .RequireAuthorization();
        
        endpoints.MapPut("/api/chords/{chordId:guid}", async (
                Guid chordId,
                UpdateChordRequest request,
                ChordService chordService,
                CancellationToken cancellationToken) =>
            {
                var chord = await chordService.UpdateChordAsync(
                    chordId,
                    request,
                    cancellationToken);

                return chord is null
                    ? Results.NotFound()
                    : Results.Ok(chord);
            })
            .RequireAuthorization();
        
        endpoints.MapDelete("/api/chords/{chordId:guid}", async (
                Guid chordId,
                ChordService chordService,
                CancellationToken cancellationToken) =>
            {
                var result = await chordService.DeleteChordAsync(
                    chordId,
                    cancellationToken);

                return result switch
                {
                    DeleteChordResult.Deleted => Results.NoContent(),
                    DeleteChordResult.NotFound => Results.NotFound(),
                    DeleteChordResult.InUse => Results.Conflict(),
                    _ => Results.Problem()
                };
            })
            .RequireAuthorization();
        
        return endpoints;
    }
}