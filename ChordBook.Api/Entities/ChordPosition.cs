namespace ChordBook.Entities;

/// <summary>
/// represents the position of a chord within a single line of lyrics
/// </summary>
public class ChordPosition
{
    public Guid Id { get; set; } = Guid.NewGuid();

    public Guid SongLineId { get; set; }

    public int CharacterIndex { get; set; }
    
    public SongLine SongLine { get; set; } = null!;
    
    public Guid ChordId { get; set; }
    
    public Chord Chord { get; set; } = null!;
}