namespace ChordBook.DTO.Songs;


/// <summary>
/// song in the song list
/// </summary>
public record SongListItemResponse(
    Guid Id,
    string Title,
    string? Artist
);