namespace ChordBook.Services.Results;

public enum DeleteChordResult
{
    Deleted,
    NotFound,
    InUse,
    Protected,
    Invalid,
    ReferencedByTemplate
}