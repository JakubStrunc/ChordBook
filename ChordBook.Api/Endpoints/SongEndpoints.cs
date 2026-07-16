using ChordBook.DTO.Songs;
using ChordBook.Services;
using ChordBook.Services.Results;

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
                string? search,
                Guid[]? categoryId,
                SongService songService,
                CancellationToken cancellationToken) =>
            {

                var songs = await songService.GetSongsAsync(
                    search,
                    categoryId ?? [],
                    cancellationToken
                );

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
                UpdateSongRequest request,
                SongService songService,
                CancellationToken cancellationToken) =>
            {
                if (string.IsNullOrWhiteSpace(request.Title))
                {
                    return Results.BadRequest(
                        "Song title is required");
                }

                var hasInvalidLineNumber = request.Lines.Any(line =>
                    line.LineNumber < 0);

                if (hasInvalidLineNumber)
                {
                    return Results.BadRequest(
                        "Line number cannot be negative");
                }

                var hasDuplicateLineNumber = request.Lines
                    .GroupBy(line => line.LineNumber)
                    .Any(group => group.Count() > 1);

                if (hasDuplicateLineNumber)
                {
                    return Results.BadRequest(
                        "Line numbers must be unique");
                }

                var hasInvalidChordPosition = request.Lines.Any(line =>
                    line.Chords.Any(chord =>
                        chord.CharacterIndex < 0 ||
                        chord.CharacterIndex > line.Text.Length));

                if (hasInvalidChordPosition)
                {
                    return Results.BadRequest(
                        "Chord position is outside the lyrics line");
                }

                try
                {
                    var song = await songService.UpdateSongAsync(
                        songId,
                        request,
                        cancellationToken);

                    return song is null
                        ? Results.NotFound()
                        : Results.Ok(song);
                }
                catch (ArgumentException exception)
                {
                    return Results.BadRequest(exception.Message);
                }
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
        
        endpoints.MapGet(
                "/api/songs/{songId:guid}/categories",
                async (
                    Guid songId,
                    SongService songService,
                    CancellationToken cancellationToken) =>
                {
                    var result = await songService.GetSongCategoriesAsync(
                        songId,
                        cancellationToken);

                    if (!result.SongExists)
                    {
                        return Results.NotFound();
                    }

                    return Results.Ok(result.Categories);
                })
            .RequireAuthorization();
        
        endpoints.MapPost(
                "/api/songs/{songId:guid}/categories/{categoryId:guid}",
                async (
                    Guid songId,
                    Guid categoryId,
                    SongService songService,
                    CancellationToken cancellationToken) =>
                {
                    var result = await songService.AddCategoryToSongAsync(
                        songId,
                        categoryId,
                        cancellationToken);

                    return result switch
                    {
                        AddSongCategoryResult.Added =>
                            Results.NoContent(),

                        AddSongCategoryResult.SongNotFound =>
                            Results.NotFound("Song was not found."),

                        AddSongCategoryResult.CategoryNotFound =>
                            Results.NotFound("Category was not found."),

                        AddSongCategoryResult.AlreadyAssigned =>
                            Results.Conflict(
                                "Category is already assigned to the song."),

                        _ => Results.BadRequest()
                    };
                })
            .RequireAuthorization();
        
        endpoints.MapDelete(
                "/api/songs/{songId:guid}/categories/{categoryId:guid}",
                async (
                    Guid songId,
                    Guid categoryId,
                    SongService songService,
                    CancellationToken cancellationToken) =>
                {
                    var isRemoved =
                        await songService.RemoveCategoryFromSongAsync(
                            songId,
                            categoryId,
                            cancellationToken);

                    return isRemoved
                        ? Results.NoContent()
                        : Results.NotFound();
                })
            .RequireAuthorization();
        

        return endpoints;
    }
}