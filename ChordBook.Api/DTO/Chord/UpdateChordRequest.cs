namespace ChordBook.DTO.Chord;

public sealed record UpdateChordRequest(
    string Name,
    string Fingering
);