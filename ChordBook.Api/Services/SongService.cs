using ChordBook.Repositories;
using ChordBook.DTO.Songs;

namespace ChordBook.Services;

public class SongService(SongRepository songRepository)
{
    public async Task<List<SongListItemResponse>> GetSongsAsync(
        CancellationToken ct)
    {
        var songs = await songRepository.GetAllSongs(ct);

        return songs
            .Select(song => new SongListItemResponse(
                song.Id,
                song.Title,
                song.Artist))
            .ToList();
    }
}