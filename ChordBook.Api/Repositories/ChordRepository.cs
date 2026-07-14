using ChordBook.Data;
using ChordBook.Entities;
using Microsoft.EntityFrameworkCore;

namespace ChordBook.Repositories;

/// <summary>
/// provides database access for chords
/// </summary>
public class ChordRepository(ChordBookDbContext dbContext)
{
    
    /// <summary>
    /// returns all chords ordered by name
    /// </summary>
    public async Task<List<Chord>> GetAllChords(
        CancellationToken ct)
    {
        return await dbContext.Chords
            .AsNoTracking()
            .OrderBy(chord => chord.Name)
            .ToListAsync(ct);
    }
    
    /// <summary>
    /// returns a chord by its identifier
    /// </summary>
    public async Task<Chord?> GetChordById(
        Guid chordId,
        CancellationToken ct)
    {
        return await dbContext.Chords
            .AsNoTracking()
            .FirstOrDefaultAsync(
                chord => chord.Id == chordId,
                ct);
    }
    
    /// <summary>
    /// persists a new chord to the database
    /// </summary>
    public async Task CreateChord(
        Chord chord,
        CancellationToken ct)
    {
        dbContext.Chords.Add(chord);

        await dbContext.SaveChangesAsync(ct);
    }
    
    /// <summary>
    /// persists changes made to an existing chord
    /// </summary>
    public async Task UpdateChord(
        Chord chord,
        CancellationToken ct)
    {
        dbContext.Chords.Update(chord);

        await dbContext.SaveChangesAsync(ct);
    }
    
    /// <summary>
    /// removes a chord from the database
    /// </summary>
    public async Task DeleteChord(
        Chord chord,
        CancellationToken ct)
    {
        dbContext.Chords.Remove(chord);

        await dbContext.SaveChangesAsync(ct);
    }
    
}