namespace ChordBook.Entities;

/// <summary>
/// Represents a guitar chord and the fret positions used to render its diagram.
/// </summary>
public class Chord
{
    public Guid Id { get; set; } = Guid.NewGuid();

    public required string Name { get; set; }

    public required string Fingering { get; set; }

    public List<ChordPosition> ChordPositions { get; set; } = [];
}