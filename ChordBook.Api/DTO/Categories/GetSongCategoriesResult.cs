namespace ChordBook.DTO.Categories;

public record GetSongCategoriesResult(
    bool SongExists,
    List<CategoryResponse> Categories
);