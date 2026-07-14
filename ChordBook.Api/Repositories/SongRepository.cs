using ChordBook.Data;
using ChordBook.DTO.Songs;
using ChordBook.Entities;
using Microsoft.EntityFrameworkCore;

namespace ChordBook.Repositories;


/// <summary>
/// provides database access for songs
/// </summary>
public class SongRepository(ChordBookDbContext dbContext)
{
    
    /// <summary>
    /// returns all songs ordered by title
    /// </summary>
    public async Task<List<Song>> GetAllSongs(
        CancellationToken ct)
    {
        return await dbContext.Songs
            .AsNoTracking()
            .OrderBy(song => song.Title)
            .ToListAsync(ct);
    }
    
    /// <summary>
    /// returns a song including its lines, chord positions and categories
    /// </summary>
    public async Task<Song?> GetSongById(
        Guid songId,
        CancellationToken ct)
    {
        return await dbContext.Songs
            .AsNoTracking()
            .Include(song => song.Lines)
                .ThenInclude(line => line.ChordPositions)
                    .ThenInclude(position => position.Chord)
            .Include(song => song.SongCategories)
                .ThenInclude(songCategory => songCategory.Category)
            .FirstOrDefaultAsync(song => song.Id == songId, ct);
    }
    
    /// <summary>
    /// retrieves a song tracked by Entity Framework for update or deletion
    /// </summary>
    public async Task<Song?> GetTrackedSongById(
        Guid songId,
        CancellationToken ct)
    {
        return await dbContext.Songs
            .FirstOrDefaultAsync(song => song.Id == songId, ct);
    }
    
    /// <summary>
    /// persists a new song to the database
    /// </summary>
    public async Task CreateSong(Song song, CancellationToken ct)
    {
        dbContext.Songs.Add(song);
        await dbContext.SaveChangesAsync(ct);
    }

    /// <summary>
    /// persists changes made to an existing song
    /// </summary>
    public async Task UpdateSong(Song song, CancellationToken ct)
    {
        dbContext.Songs.Update(song);
        await dbContext.SaveChangesAsync(ct);
    }
    
    /// <summary>
    /// removes a song from the database
    /// </summary>
    public async Task DeleteSong(Song song, CancellationToken ct) 
    {
        dbContext.Songs.Remove(song);
        await dbContext.SaveChangesAsync(ct);
        
    }
    

}