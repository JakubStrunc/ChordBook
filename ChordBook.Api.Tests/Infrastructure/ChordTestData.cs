using System.Net.Http.Json;
using ChordBook.DTO.Chord;
using ChordBook.DTO.Chord;

namespace ChordBook.Api.Tests.Infrastructure;

public static class ChordTestData
{
    
    /// <summary>
    /// helper methods for creating test chord 
    /// </summary>
    public static async Task<ChordResponse> CreateChordAsync(
        HttpClient client,
        string name = "Am",
        string fingering = "x02210")
    {
        
        // creating chord
        var request = new CreateChordRequest(
            name,
            fingering);

        var response = await client.PostAsJsonAsync(
            "/api/chords",
            request);
        
        
        // verify that chord created successfully
        response.EnsureSuccessStatusCode();

        var chord = await response.Content
            .ReadFromJsonAsync<ChordResponse>();

        if (chord is null)
            throw new InvalidOperationException();

        return chord;
    }
}