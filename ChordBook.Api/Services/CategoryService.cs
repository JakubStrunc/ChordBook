using ChordBook.DTO.Categories;
using ChordBook.Entities;
using ChordBook.Repositories;
using Microsoft.EntityFrameworkCore;

namespace ChordBook.Services;

/// <summary>
/// provides business logic for managing songs
/// </summary>
public class CategoryService(CategoryRepository categoryRepository)
{

    /// <summary>
    /// returns all songs ordered by title
    /// </summary>
    public async Task<List<CategoryResponse>> GetCategoriesAsync(
        CancellationToken ct)
    {
        var categories = await categoryRepository.GetAllCategoriesAsync(ct);

        return categories
            .Select(category => new CategoryResponse(
                category.Id,
                category.Name
            ))
            .ToList();
    }

    /// <summary>
    /// creates a new category
    /// </summary>
    public async Task<Guid> CreateCategoryAsync(
        CreateCategoryRequest createCategoryRequest,
        CancellationToken ct
    )
    {
        var category = new Category
        {
            Name = createCategoryRequest.Name
        };
        
        await categoryRepository.CreateCategory(category, ct);
        
        return category.Id;
    }
    
    /// <summary>
    /// deletes a category
    /// </summary>
    public async Task<bool> DeleteCategoryAsync(
        Guid categoryId,
        CancellationToken ct
    )
    {
        var category = await categoryRepository.GetTrackedCategoryById(categoryId, ct);
        
        if (category == null) return false;
        
        await categoryRepository.DeleteCategory(category, ct);
        
        return true;
    }

}