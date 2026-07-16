using ChordBook.DTO.Categories;
using ChordBook.Repositories;
using ChordBook.DTO.Songs;
using ChordBook.Entities;
using ChordBook.Services.Results;

namespace ChordBook.Services;


/// <summary>
/// provides business logic for managing songs
/// </summary>
public class SongService(SongRepository songRepository)
{
    
    /// <summary>
    /// returns all songs by search text and categories ordered by title
    /// </summary>
    public async Task<List<SongListItemResponse>> GetSongsAsync(
        string? search,
        IReadOnlyCollection<Guid> categoryIds,
        CancellationToken ct)
    {

        var songs = await songRepository.GetSongs(
            search,
            categoryIds,
            ct
        );

        return songs
            .Select(song => new SongListItemResponse(
                song.Id,
                song.Title,
                song.Artist))
            .ToList();
    }
    
    /// <summary>
    /// returns a song with its categories, lyrics and chord positions
    /// </summary>
    public async Task<SongDetailResponse?> GetSongAsync(
        Guid id,
        CancellationToken ct)
    {
        var song = await songRepository.GetSongById(id, ct);


        return song == null
            ? null
            : new SongDetailResponse
                (
                    song.Id,
                    song.Title,
                    song.Artist,
                    song.SongCategories
                        .Select(songCategory => songCategory.Category.Name)
                        .OrderBy(name => name)
                        .ToList(),
                    song.Lines
                        .Select(songLine => new SongLineResponse
                            (
                                songLine.LineNumber,
                                songLine.Text,
                                songLine.ChordPositions
                                    .Select(chordPosition => new ChordPositionResponse
                                        (
                                            chordPosition.CharacterIndex,
                                            new ChordResponse
                                            (
                                                chordPosition.Chord.Id,
                                                chordPosition.Chord.Name,
                                                chordPosition.Chord.Fingering
                                            )
                                        )
                                    )
                                    .OrderBy(chord => chord.CharacterIndex)
                                    .ToList()
                            )
                        )
                        .OrderBy(line => line.LineNumber)
                        .ToList()
                
                );
    }

    /// <summary>
    /// creates a new song
    /// </summary>
    public async Task<Guid> CreateSongAsync(
        CreateSongRequest createSongRequest,
        CancellationToken ct
    )
    {
        var song = new Song
        {
            Title = createSongRequest.Title,
            Artist = createSongRequest.Artist,
        };
        
        await songRepository.CreateSong(song, ct);
        
        return song.Id;
    }

    /// <summary>
    /// updates an existing song including lyrics and chord positions
    /// </summary>
    public async Task<SongDetailResponse?> UpdateSongAsync(
        Guid songId,
        UpdateSongRequest request,
        CancellationToken ct)
    {
        var song = await songRepository.GetTrackedSongById(
            songId,
            ct);

        if (song is null)
        {
            return null;
        }

        var chordIds = request.Lines
            .SelectMany(line => line.Chords)
            .Select(chord => chord.ChordId)
            .Distinct()
            .ToList();

        var allChordsExist = await songRepository.AllChordsExist(
            chordIds,
            ct);

        if (!allChordsExist)
        {
            throw new ArgumentException(
                "One or more selected chords do not exist");
        }

        song.Title = request.Title.Trim();

        song.Artist = string.IsNullOrWhiteSpace(request.Artist)
            ? null
            : request.Artist.Trim();

        song.UpdatedAt = DateTime.UtcNow;

        var newLines = request.Lines
            .OrderBy(line => line.LineNumber)
            .Select(lineRequest =>
            {
                var songLine = new SongLine
                {
                    SongId = song.Id,
                    LineNumber = lineRequest.LineNumber,
                    Text = lineRequest.Text
                };

                foreach (var chordRequest in lineRequest.Chords
                             .OrderBy(chord => chord.CharacterIndex))
                {
                    songLine.ChordPositions.Add(
                        new ChordPosition
                        {
                            CharacterIndex =
                                chordRequest.CharacterIndex,
                            ChordId =
                                chordRequest.ChordId
                        });
                }

                return songLine;
            })
            .ToList();

        await songRepository.ReplaceSongLines(
            song.Id,
            newLines,
            ct);

        await songRepository.SaveSong(ct);

        return await GetSongAsync(
            songId,
            ct);
    }
    
    /// <summary>
    /// deletes a song
    /// </summary>
    public async Task<bool> DeleteSongAsync(
        Guid songId,
        CancellationToken ct
    )
    {
        var song = await songRepository.GetTrackedSongById(songId, ct);
        
        if (song == null) return false;


        await songRepository.DeleteSong(song, ct);
        
        return true;
    }
    
    /// <summary>
    /// returns categories assigned to a song
    /// </summary>
    public async Task<GetSongCategoriesResult> GetSongCategoriesAsync(
        Guid songId,
        CancellationToken ct)
    {
        var songExists = await songRepository.SongExistsAsync(
            songId,
            ct);

        if (!songExists)
        {
            return new GetSongCategoriesResult(
                false,
                []);
        }

        var categories = await songRepository.GetSongCategoriesAsync(
            songId,
            ct);

        return new GetSongCategoriesResult(
            true,
            categories);
    }

    /// <summary>
    /// assigns an existing category to a song
    /// </summary>
    public async Task<AddSongCategoryResult> AddCategoryToSongAsync(
        Guid songId,
        Guid categoryId,
        CancellationToken ct)
    {
        var songExists = await songRepository.SongExistsAsync(
            songId,
            ct);

        if (!songExists)
        {
            return AddSongCategoryResult.SongNotFound;
        }

        var categoryExists = await songRepository.CategoryExistsAsync(
            categoryId,
            ct);

        if (!categoryExists)
        {
            return AddSongCategoryResult.CategoryNotFound;
        }

        var alreadyAssigned =
            await songRepository.SongCategoryExistsAsync(
                songId,
                categoryId,
                ct);

        if (alreadyAssigned)
        {
            return AddSongCategoryResult.AlreadyAssigned;
        }

        await songRepository.AddCategoryToSongAsync(
            songId,
            categoryId,
            ct);

        return AddSongCategoryResult.Added;
    }

    /// <summary>
    /// removes a category assignment from a song
    /// </summary>
    public async Task<bool> RemoveCategoryFromSongAsync(
        Guid songId,
        Guid categoryId,
        CancellationToken ct)
    {
        return await songRepository.RemoveCategoryFromSongAsync(
            songId,
            categoryId,
            ct);
    }
}