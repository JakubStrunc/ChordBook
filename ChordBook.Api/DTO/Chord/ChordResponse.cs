namespace ChordBook.DTO.Chord;

public record ChordResponse(
    Guid Id,
    string Name,
    string Fingering
);