namespace ChordBook.Entities;


/// <summary>
/// represents a category that can be assigned to one or more songs
/// </summary>
public class Category
{
    public Guid Id { get; set; } = Guid.NewGuid();

    public required string Name { get; set; }

    public List<SongCategory> SongCategories { get; set; } = [];
}