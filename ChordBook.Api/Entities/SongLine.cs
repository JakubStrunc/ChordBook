namespace ChordBook.Entities;


/// <summary>
/// represents a single line of lyrics within a song
/// </summary>
public class SongLine
{
    public Guid Id { get; set; } = Guid.NewGuid();

    public Guid SongId { get; set; }

    public int LineNumber { get; set; }

    public string Text { get; set; } = string.Empty;

    public Song Song { get; set; } = null!;

    public List<ChordPosition> ChordPositions { get; set; } = [];
}