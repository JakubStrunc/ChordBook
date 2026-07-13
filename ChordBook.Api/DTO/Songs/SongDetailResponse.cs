namespace ChordBook.DTO.Songs;

public record SongDetailResponse(
    Guid Id,
    string Title,
    string? Artist,
    List<string> Categories,
    List<SongLineResponse> Lines
);