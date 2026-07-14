using ChordBook.Data;
using ChordBook.DTO.Categories;
using ChordBook.Entities;
using Microsoft.EntityFrameworkCore;

namespace ChordBook.Repositories;

/// <summary>
/// provides database access for categories
/// </summary>
public class CategoryRepository(ChordBookDbContext dbContext)
{

    /// <summary>
    /// returns all categories ordered by name
    /// </summary>
    public async Task<List<Category>> GetAllCategoriesAsync(
        CancellationToken ct)
    {
        return await dbContext.Categories
            .AsNoTracking()
            .OrderBy(category => category.Name)
            .ToListAsync(ct);
    }
    
    /// <summary>
    /// returns a category
    /// </summary>
    public async Task<Category?> GetTrackedCategoryById(
        Guid categoryId,
        CancellationToken ct)
    {
        return await dbContext.Categories
            .FirstOrDefaultAsync(category => category.Id == categoryId, ct);
    }
    
    /// <summary>
    /// persists a new category to the database
    /// </summary>
    public async Task CreateCategory(Category category, CancellationToken ct)
    {
        dbContext.Categories.Add(category);
        await dbContext.SaveChangesAsync(ct);
    }
    
    /// <summary>
    /// removes a category from the database
    /// </summary>
    public async Task DeleteCategory(Category category, CancellationToken ct) 
    {
        dbContext.Categories.Remove(category);
        await dbContext.SaveChangesAsync(ct);
        
    }
}