using ChordBook.Data;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// add service to container
builder.Services.AddOpenApi();
