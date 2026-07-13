using ChordBook.Data;
using ChordBook.Endpoints;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// add service to container
builder.Services.AddOpenApi();

// database
builder.Services.AddDbContext<ChordBookDbContext>(options =>
    options.UseSqlServer(
        builder.Configuration.GetConnectionString("ChordBook")));

var app = builder.Build();

// available only in development
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();

// endpoints
app.MapHealthEndpoints();

app.Run();