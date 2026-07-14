using System.Net.Http.Json;
using ChordBook.DTO.Songs;

namespace ChordBook.Api.Tests.Infrastructure;

public static class SongTestData
{
    /// <summary>
    /// helper methods for creating test song 
    /// </summary>
    public static async Task<SongDetailResponse> CreateSongAsync(
        HttpClient client,
        string title = "Test song",
        string? artist = "Test artist")
    {
        // creating song
        var request = new CreateSongRequest(
            title,
            artist);

        var response = await client.PostAsJsonAsync(
            "/api/songs",
            request);
        
        // verify that song created successfully
        response.EnsureSuccessStatusCode();

        var song = await response.Content
            .ReadFromJsonAsync<SongDetailResponse>();

        if (song is null)
        {
            throw new InvalidOperationException(
                "Created song response was empty.");
        }

        return song;
    }
}