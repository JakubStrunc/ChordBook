namespace ChordBook.Entities;

/// <summary>
/// represents a song containing lyrics, chord positions and assigned categories
/// </summary>
public class Song
{
    public Guid Id { get; set; } = Guid.NewGuid();

    public required string Title { get; set; }

    public string? Artist { get; set; }

    public DateTimeOffset CreatedAt { get; set; } = DateTimeOffset.UtcNow;

    public DateTimeOffset UpdatedAt { get; set; } = DateTimeOffset.UtcNow;

    public List<SongLine> Lines { get; set; } = [];

    public List<SongCategory> SongCategories { get; set; } = [];
}