namespace ChordBook.DTO.Songs;

public record CreateSongRequest(
    string Title, 
    string? Artist
);