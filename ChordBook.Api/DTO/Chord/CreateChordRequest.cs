namespace ChordBook.DTO.Chord;

public record CreateChordRequest(
    string Name,
    string Fingering
);