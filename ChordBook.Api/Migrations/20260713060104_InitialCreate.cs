using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace ChordBook.Migrations
{
    /// <inheritdoc />
    public partial class InitialCreate : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Categories",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(100)", maxLength: 100, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Categories", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Chords",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(20)", maxLength: 20, nullable: false),
                    Fingering = table.Column<string>(type: "nvarchar(50)", maxLength: 50, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Chords", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Songs",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Title = table.Column<string>(type: "nvarchar(200)", maxLength: 200, nullable: false),
                    Artist = table.Column<string>(type: "nvarchar(200)", maxLength: 200, nullable: true),
                    CreatedAt = table.Column<DateTimeOffset>(type: "datetimeoffset", nullable: false),
                    UpdatedAt = table.Column<DateTimeOffset>(type: "datetimeoffset", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Songs", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "SongCategories",
                columns: table => new
                {
                    SongId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    CategoryId = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_SongCategories", x => new { x.SongId, x.CategoryId });
                    table.ForeignKey(
                        name: "FK_SongCategories_Categories_CategoryId",
                        column: x => x.CategoryId,
                        principalTable: "Categories",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_SongCategories_Songs_SongId",
                        column: x => x.SongId,
                        principalTable: "Songs",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "SongLines",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    SongId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    LineNumber = table.Column<int>(type: "int", nullable: false),
                    Text = table.Column<string>(type: "nvarchar(max)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_SongLines", x => x.Id);
                    table.CheckConstraint("CK_SongLine_LineNumber_NonNegative", "\"LineNumber\" >= 0");
                    table.ForeignKey(
                        name: "FK_SongLines_Songs_SongId",
                        column: x => x.SongId,
                        principalTable: "Songs",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "ChordPositions",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    SongLineId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    CharacterIndex = table.Column<int>(type: "int", nullable: false),
                    ChordId = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ChordPositions", x => x.Id);
                    table.CheckConstraint("CK_ChordPosition_CharacterIndex_NonNegative", "\"CharacterIndex\" >= 0");
                    table.ForeignKey(
                        name: "FK_ChordPositions_Chords_ChordId",
                        column: x => x.ChordId,
                        principalTable: "Chords",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                    table.ForeignKey(
                        name: "FK_ChordPositions_SongLines_SongLineId",
                        column: x => x.SongLineId,
                        principalTable: "SongLines",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_Categories_Name",
                table: "Categories",
                column: "Name",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_ChordPositions_ChordId",
                table: "ChordPositions",
                column: "ChordId");

            migrationBuilder.CreateIndex(
                name: "IX_ChordPositions_SongLineId_CharacterIndex",
                table: "ChordPositions",
                columns: new[] { "SongLineId", "CharacterIndex" },
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_Chords_Name",
                table: "Chords",
                column: "Name",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_SongCategories_CategoryId",
                table: "SongCategories",
                column: "CategoryId");

            migrationBuilder.CreateIndex(
                name: "IX_SongLines_SongId_LineNumber",
                table: "SongLines",
                columns: new[] { "SongId", "LineNumber" },
                unique: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "ChordPositions");

            migrationBuilder.DropTable(
                name: "SongCategories");

            migrationBuilder.DropTable(
                name: "Chords");

            migrationBuilder.DropTable(
                name: "SongLines");

            migrationBuilder.DropTable(
                name: "Categories");

            migrationBuilder.DropTable(
                name: "Songs");
        }
    }
}
