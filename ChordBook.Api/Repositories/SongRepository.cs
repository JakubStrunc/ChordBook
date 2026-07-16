using ChordBook.Data;
using ChordBook.DTO.Categories;
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
    public async Task<List<Song>> GetSongs(
        string? search,
        IReadOnlyCollection<Guid> categoryIds,
        CancellationToken ct)
    {
        var query = dbContext.Songs
            .AsNoTracking()
            .AsQueryable();
        
        if (!string.IsNullOrWhiteSpace(search))
        {
            var normalizedSearch = search.Trim();

            query = query.Where(song =>
                song.Title.Contains(normalizedSearch) ||
                (
                    song.Artist != null &&
                    song.Artist.Contains(normalizedSearch)
                )
            );
        }
        
        if (categoryIds.Count > 0)
        {
            query = query.Where(song =>
                categoryIds.All(categoryId =>
                    song.SongCategories.Any(songCategory =>
                        songCategory.CategoryId == categoryId
                    )
                )
            );
        }
        
        return await query
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
            .FirstOrDefaultAsync(
                song => song.Id == songId,
                ct);
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
    public async Task SaveSong(CancellationToken ct)
    {
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
    
    /// <summary>
    /// returns categories assigned to a song
    /// </summary>
    public async Task<List<CategoryResponse>> GetSongCategoriesAsync(
        Guid songId,
        CancellationToken ct)
    {
        return await dbContext.SongCategories
            .AsNoTracking()
            .Where(songCategory => songCategory.SongId == songId)
            .OrderBy(songCategory => songCategory.Category.Name)
            .Select(songCategory => new CategoryResponse(
                songCategory.Category.Id,
                songCategory.Category.Name))
            .ToListAsync(ct);
    }
    
    /// <summary>
    /// checks whether a song exists
    /// </summary>
    public async Task<bool> SongExistsAsync(
        Guid songId,
        CancellationToken ct)
    {
        return await dbContext.Songs
            .AnyAsync(song => song.Id == songId, ct);
    }
    
    /// <summary>
    /// checks whether a category exists
    /// </summary>
    public async Task<bool> CategoryExistsAsync(
        Guid categoryId,
        CancellationToken ct)
    {
        return await dbContext.Categories
            .AnyAsync(category => category.Id == categoryId, ct);
    }

    /// <summary>
    /// checks whether the category is already assigned to the song
    /// </summary>
    public async Task<bool> SongCategoryExistsAsync(
        Guid songId,
        Guid categoryId,
        CancellationToken ct)
    {
        return await dbContext.SongCategories
            .AnyAsync(
                songCategory =>
                    songCategory.SongId == songId &&
                    songCategory.CategoryId == categoryId,
                ct);
    }

    /// <summary>
    /// assigns a category to a song
    /// </summary>
    public async Task AddCategoryToSongAsync(
        Guid songId,
        Guid categoryId,
        CancellationToken ct)
    {
        var songCategory = new SongCategory
        {
            SongId = songId,
            CategoryId = categoryId
        };

        dbContext.SongCategories.Add(songCategory);

        await dbContext.SaveChangesAsync(ct);
    }

    /// <summary>
    /// removes a category assignment from a song
    /// </summary>
    public async Task<bool> RemoveCategoryFromSongAsync(
        Guid songId,
        Guid categoryId,
        CancellationToken ct)
    {
        var songCategory = await dbContext.SongCategories
            .FirstOrDefaultAsync(
                songCategory =>
                    songCategory.SongId == songId &&
                    songCategory.CategoryId == categoryId,
                ct);

        if (songCategory is null)
        {
            return false;
        }

        dbContext.SongCategories.Remove(songCategory);

        await dbContext.SaveChangesAsync(ct);

        return true;
    }
    
    /// <summary>
    /// checks whether all specified chords exist
    /// </summary>
    public async Task<bool> AllChordsExist(
        IReadOnlyCollection<Guid> chordIds,
        CancellationToken ct)
    {
        var distinctChordIds = chordIds
            .Distinct()
            .ToList();

        if (distinctChordIds.Count == 0)
        {
            return true;
        }

        var existingChordCount = await dbContext.Chords
            .CountAsync(
                chord => distinctChordIds.Contains(chord.Id),
                ct);

        return existingChordCount == distinctChordIds.Count;
    }
    
    /// <summary>
    /// replaces all lyrics lines and chord positions of a song
    /// </summary>
    public async Task ReplaceSongLines(
        Guid songId,
        IReadOnlyCollection<SongLine> newLines,
        CancellationToken ct)
    {
        await dbContext.ChordPositions
            .Where(position =>
                position.SongLine.SongId == songId)
            .ExecuteDeleteAsync(ct);

        await dbContext.SongLines
            .Where(line => line.SongId == songId)
            .ExecuteDeleteAsync(ct);

        if (newLines.Count > 0)
        {
            dbContext.SongLines.AddRange(newLines);
        }
    }
    
}