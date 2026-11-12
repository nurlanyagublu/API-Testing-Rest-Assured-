# Football API Tests

A Java API testing project using REST Assured and JUnit 5 to verify football data from [API-FOOTBALL](https://www.api-football.com/).

## What’s tested

- Time zones and countries
- Leagues and seasons
- Teams and team statistics
- League standings
- Venues

The tests include both direct response assertions and tests that load team and league data dynamically.

## Requirements

- Java 16 or later
- An API-FOOTBALL API key
- The project’s configured Java build tool and dependencies

## API key setup

The tests read the API key from the `API_KEY` environment variable. Keep your key private; do not commit it to the repository.

In PowerShell:

```powershell
$env:API_KEY = "your_api_key"


Then run the tests using your project’s build tool or IDE.

Project structure
AssertionsInsideOfTheChainOfFunctionsTest.java — assertions within REST Assured request chains
ExtractingResponseTest.java — extracts response values and verifies them with JUnit assertions
Task3/ApiLoadedTestDataTest.java — loads football test data from the API before running tests
Task3/FootballTestData.java — holds the loaded team and league data
Task3/FootballTestDataLoader.java — retrieves test data from the API
