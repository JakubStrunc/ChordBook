using Microsoft.EntityFrameworkCore;
using ChordBook.Entities;

namespace ChordBook.Data;


/// <summary>
/// configures database tables, relationships, indexes and integrity constraints.
/// </summary>
public class ChordBookDbContext(
    DbContextOptions<ChordBookDbContext> options)
    : DbContext(options)
{
    public DbSet<Song> Songs => Set<Song>();

    public DbSet<SongLine> SongLines => Set<SongLine>();

    public DbSet<ChordPosition> ChordPositions => Set<ChordPosition>();

    public DbSet<Category> Categories => Set<Category>();

    public DbSet<SongCategory> SongCategories => Set<SongCategory>();
    
    public DbSet<Chord> Chords => Set<Chord>();
    
    /// <summary>
    /// configures the database model using EF.
    /// </summary>
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        ConfigureSong(modelBuilder);
        ConfigureSongLine(modelBuilder);
        ConfigureChordPosition(modelBuilder);
        ConfigureCategory(modelBuilder);
        ConfigureSongCategory(modelBuilder);
        ConfigureChord(modelBuilder);
    }
    
    /// <summary>
    /// configures Song entity
    /// </summary>
    private static void ConfigureSong(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Song>(entity =>
        {
            entity.HasKey(song => song.Id);

            entity.Property(song => song.Title)
                .HasMaxLength(200)
                .IsRequired();

            entity.Property(song => song.Artist)
                .HasMaxLength(200);

            entity.Property(song => song.CreatedAt)
                .IsRequired();

            entity.Property(song => song.UpdatedAt)
                .IsRequired();
        });
    }
    
    /// <summary>
    /// configures SongLine entity
    /// </summary>
    private static void ConfigureSongLine(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<SongLine>(entity =>
        {
            entity.HasKey(line => line.Id);

            entity.Property(line => line.Text)
                .IsRequired();

            entity.Property(line => line.LineNumber)
                .IsRequired();
            
            // has to be unique and can be used for faster search
            entity.HasIndex(line => new
            {
                line.SongId,
                line.LineNumber
            }).IsUnique();
            
            // integrity constraint that variable LineNumber cannot be negative
            entity.ToTable(table =>
                table.HasCheckConstraint(
                    "CK_SongLine_LineNumber_NonNegative",
                    "\"LineNumber\" >= 0"));
            
            // one song has many lines
            // deleting song will also delete  its lines
            entity.HasOne(line => line.Song)
                .WithMany(song => song.Lines)
                .HasForeignKey(line => line.SongId)
                .OnDelete(DeleteBehavior.Cascade);
        });
    }
    
    /// <summary>
    /// configures ChordPosition entity
    /// </summary>
    private static void ConfigureChordPosition(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<ChordPosition>(entity =>
        {
            entity.HasKey(position => position.Id);

            entity.Property(position => position.CharacterIndex)
                .IsRequired();
            
            // integrity constraint that variable CharacterIndex cannot be negative
            entity.ToTable(table =>
                table.HasCheckConstraint(
                    "CK_ChordPosition_CharacterIndex_NonNegative",
                    "\"CharacterIndex\" >= 0"));
            
            // only one chord can be placed at specific position in a song line
            entity.HasIndex(position => new
            {
                position.SongLineId,
                position.CharacterIndex
            }).IsUnique();

            // one song line has many chords
            // deleting song line will also delete chords on it
            entity.HasOne(position => position.SongLine)
                .WithMany(line => line.ChordPositions)
                .HasForeignKey(position => position.SongLineId)
                .OnDelete(DeleteBehavior.Cascade);
            
            // one chord can be used in many song positions
            // a chord that is currently used cannot be deleted
            entity.HasOne(position => position.Chord)
                .WithMany(chord => chord.ChordPositions)
                .HasForeignKey(position => position.ChordId)
                .OnDelete(DeleteBehavior.Restrict);
        });
    }
    
    /// <summary>
    /// configures Category entity
    /// </summary>
    private static void ConfigureCategory(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Category>(entity =>
        {
            entity.HasKey(category => category.Id);

            entity.Property(category => category.Name)
                .HasMaxLength(100)
                .IsRequired();

            entity.HasIndex(category => category.Name)
                .IsUnique();
        });
    }
    
    /// <summary>
    /// configures the N:M relationship between Song and Category tables
    /// </summary>
    private static void ConfigureSongCategory(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<SongCategory>(entity =>
        {
            entity.HasKey(songCategory => new
            {
                songCategory.SongId,
                songCategory.CategoryId
            });

            entity.HasOne(songCategory => songCategory.Song)
                .WithMany(song => song.SongCategories)
                .HasForeignKey(songCategory => songCategory.SongId)
                .OnDelete(DeleteBehavior.Cascade);

            entity.HasOne(songCategory => songCategory.Category)
                .WithMany(category => category.SongCategories)
                .HasForeignKey(songCategory => songCategory.CategoryId)
                .OnDelete(DeleteBehavior.Cascade);
        });
    }
    
    /// <summary>
    /// configures the Chord entity
    /// </summary>
    private static void ConfigureChord(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Chord>(entity =>
        {
            entity.HasKey(chord => chord.Id);

            entity.Property(chord => chord.Name)
                .HasMaxLength(20)
                .IsRequired();

            entity.Property(chord => chord.Fingering)
                .HasMaxLength(50)
                .IsRequired();

            // Every chord name must be unique.
            entity.HasIndex(chord => chord.Name)
                .IsUnique();
        });
    }
}