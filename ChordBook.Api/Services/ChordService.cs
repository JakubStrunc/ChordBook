using ChordBook.DTO.Chord;
using ChordBook.Entities;
using ChordBook.Repositories;
using ChordBook.Services.Results;

namespace ChordBook.Services;

public class ChordService(ChordRepository chordRepository)
{
    /// <summary>
    /// returns all supported chords
    /// </summary>
    public async Task<List<ChordResponse>> GetChordsAsync(
        CancellationToken ct)
    {
        var chords = await chordRepository.GetAllChords(ct);

        return chords
            .Select(chord => new ChordResponse(
                chord.Id,
                chord.Name,
                chord.Fingering))
            .ToList();
    }
    
    /// <summary>
    /// returns a chord by its identifier
    /// </summary>
    public async Task<ChordResponse?> GetChordAsync(
        Guid chordId,
        CancellationToken ct)
    {
        var chord = await chordRepository.GetChordById(chordId, ct);

        return chord == null
            ? null
            : new ChordResponse(
                chord.Id,
                chord.Name,
                chord.Fingering);
    }
    
    /// <summary>
    /// creates a new chord
    /// </summary>
    public async Task<Guid?> CreateChordAsync(
        CreateChordRequest request,
        CancellationToken ct)
    {
        if (string.IsNullOrWhiteSpace(request.Name) ||
            string.IsNullOrWhiteSpace(request.Fingering))
        {
            return null;
        }
        
        var chord = new Chord
        {
            Name = request.Name,
            Fingering = request.Fingering
        };

        await chordRepository.CreateChord(chord, ct);

        return chord.Id;
    }
    
    /// <summary>
    /// updates an existing chord
    /// </summary>
    public async Task<ChordResponse?> UpdateChordAsync(
        Guid chordId,
        UpdateChordRequest request,
        CancellationToken ct)
    {
        var chord = await chordRepository.GetChordById(chordId, ct);

        if (chord is null)
        {
            return null;
        }

        chord.Name = request.Name;
        chord.Fingering = request.Fingering;

        await chordRepository.UpdateChord(chord, ct);

        return new ChordResponse(
            chord.Id,
            chord.Name,
            chord.Fingering);
    }
    
    /// <summary>
    /// deletes a chord
    /// </summary>
    public async Task<DeleteChordResult> DeleteChordAsync(
        Guid chordId,
        CancellationToken ct)
    {
        var chord = await chordRepository.GetChordById(chordId, ct);

        if (chord is null)
        {
            return DeleteChordResult.NotFound;
        }

        if (chord.ChordPositions.Any())
        {
            return DeleteChordResult.InUse;
        }

        await chordRepository.DeleteChord(chord, ct);

        return DeleteChordResult.Deleted;
    }
}