namespace ChordBook.DTO.Songs;

public record UpdateSongRequest(
    string Title,
    string? Artist,
    List<UpdateSongLineRequest> Lines
);

public record UpdateSongLineRequest(
    int LineNumber,
    string Text,
    List<UpdateChordPositionRequest> Chords
);

public record UpdateChordPositionRequest(
    int CharacterIndex,
    Guid ChordId
);