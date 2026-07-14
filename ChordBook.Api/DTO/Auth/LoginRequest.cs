namespace ChordBook.DTO.Auth;

public record LoginRequest(
    string Username,
    string Password
);