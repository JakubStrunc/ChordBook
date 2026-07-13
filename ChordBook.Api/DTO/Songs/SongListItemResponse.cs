namespace ChordBook.DTO.Songs;

/// <summary>
/// Song displayed in the song list.
/// </summary>
public record SongListItemResponse(
    Guid Id,
    string Title,
    string? Artist
);