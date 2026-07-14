using System.Net.Http.Json;
using ChordBook.DTO.Chord;
using ChordBook.DTO.Chord;

namespace ChordBook.Api.Tests.Infrastructure;

public static class ChordTestData
{
    public static async Task<ChordResponse> CreateChordAsync(
        HttpClient client,
        string name = "Am",
        string fingering = "x02210")
    {
        var request = new CreateChordRequest(
            name,
            fingering);

        var response = await client.PostAsJsonAsync(
            "/api/chords",
            request);

        response.EnsureSuccessStatusCode();

        var chord = await response.Content
            .ReadFromJsonAsync<ChordResponse>();

        if (chord is null)
            throw new InvalidOperationException();

        return chord;
    }
}