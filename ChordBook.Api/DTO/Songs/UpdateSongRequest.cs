namespace ChordBook.DTO.Songs;

public sealed record UpdateSongRequest(
    string Title,
    string? Artist
);