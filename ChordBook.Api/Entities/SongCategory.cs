namespace ChordBook.Entities;


/// <summary>
/// represents the M:N relationship between songs and categories
/// </summary>
public class SongCategory
{
    public Guid SongId { get; set; }

    public Guid CategoryId { get; set; }

    public Song Song { get; set; } = null!;

    public Category Category { get; set; } = null!;
}