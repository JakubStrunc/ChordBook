using ChordBook.Data;
using ChordBook.Entities;
using Microsoft.EntityFrameworkCore;

namespace ChordBook.Repositories;

public class SongRepository(ChordBookDbContext dbContext)
{
    public async Task<List<Song>> GetAllSongs(
        CancellationToken ct)
    {
        return await dbContext.Songs
            .AsNoTracking()
            .OrderBy(song => song.Title)
            .ToListAsync(ct);
    }
}