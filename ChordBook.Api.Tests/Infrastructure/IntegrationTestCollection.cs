namespace ChordBook.Api.Tests.Infrastructure;

[CollectionDefinition("IntegrationTests")]
public sealed class IntegrationTestCollection
    : ICollectionFixture<CustomWebApplicationFactory>
{
}