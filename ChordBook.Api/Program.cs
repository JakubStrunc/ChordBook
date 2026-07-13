using System.Text;
using ChordBook.Data;
using ChordBook.Endpoints;
using ChordBook.Repositories;
using ChordBook.Services;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;

DotNetEnv.Env.Load();

var builder = WebApplication.CreateBuilder(args);

// add service to container
builder.Services.AddOpenApi();

// database
builder.Services.AddDbContext<ChordBookDbContext>(options =>
    options.UseSqlServer(
        builder.Configuration.GetConnectionString("ChordBook")));

// JWT configuration
var jwtKey = builder.Configuration["Jwt:Key"]
             ?? throw new InvalidOperationException("JWT key is missing.");

var jwtIssuer = builder.Configuration["Jwt:Issuer"]
                ?? throw new InvalidOperationException("JWT issuer is missing.");

var jwtAudience = builder.Configuration["Jwt:Audience"]
                  ?? throw new InvalidOperationException("JWT audience is missing.");

// authentication validates the JWT from the Authorization header.
builder.Services
    .AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidIssuer = jwtIssuer,

            ValidateAudience = true,
            ValidAudience = jwtAudience,

            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(jwtKey)),

            ValidateLifetime = true,

            // token expires exactly at its expiration time
            ClockSkew = TimeSpan.Zero
        };
    });

builder.Services.AddAuthorization();
builder.Services.AddScoped<SongService>();
builder.Services.AddScoped<SongRepository>();

var app = builder.Build();

// available only in development
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseHttpsRedirection();

// middleware order is important
app.UseAuthentication();
app.UseAuthorization();

// public endpoints
app.MapHealthEndpoints();

// secure endpoints
// app.MapAuthEndpoints();
app.MapSongEndpoints();



app.Run();

public partial class Program;