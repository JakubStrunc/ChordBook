namespace ChordBook.DTO.Songs;

public record SongLineResponse(
    int LineNumber,
    string Text,
    List<ChordPositionResponse> Chords
);

public record ChordPositionResponse(
    int CharacterIndex,
    ChordResponse Chord
);

public record ChordResponse(
    Guid Id,
    string Name,
    string? Fingering
);