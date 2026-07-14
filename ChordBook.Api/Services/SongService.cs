using ChordBook.Repositories;
using ChordBook.DTO.Songs;
using ChordBook.Entities;

namespace ChordBook.Services;


/// <summary>
/// provides business logic for managing songs
/// </summary>
public class SongService(SongRepository songRepository)
{
    
    /// <summary>
    /// returns all songs ordered by title
    /// </summary>
    public async Task<List<SongListItemResponse>> GetSongsAsync(
        CancellationToken ct)
    {
        var songs = await songRepository.GetAllSongs(ct);

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
    /// updates an existing song
    /// </summary>
    public async Task<SongDetailResponse?> UpdateSongAsync(
        Guid songId,
        UpdateSongRequest updateSongRequest,
        CancellationToken ct)
    {
        var song = await songRepository.GetTrackedSongById(songId, ct);
        
        if (song == null) return null;
        
        song.Title = updateSongRequest.Title;
        song.Artist = updateSongRequest.Artist;
        song.UpdatedAt = DateTime.UtcNow;
        
        await songRepository.UpdateSong(song, ct);
        
        return new SongDetailResponse(
            song.Id,
            song.Title,
            song.Artist,
            [],
            []
        );
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
}