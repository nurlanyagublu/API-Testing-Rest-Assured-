package API.Task3;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class FootballTestDataLoader {
    public static FootballTestData load() {

        Response teamResponse =
                given()
                        .queryParam("name", "Manchester United")
                .when()
                        .get("/teams")
                .then()
                        .extract()
                        .response();

        int teamId = teamResponse.path("response[0].team.id");
        String teamName = teamResponse.path("response[0].team.name");
        int venueId = teamResponse.path("response[0].venue.id");

        Response leagueResponse =
                given()
                        .queryParam("name", "Premier League")
                        .queryParam("country", "England")
                        .queryParam("season", 2023)
                .when()
                        .get("/leagues")
                .then()
                        .extract()
                        .response();

        int leagueId = leagueResponse.path("response[0].league.id");
        int availableSeason = leagueResponse.path("response[0].seasons[0].year");

        return new FootballTestData(
                teamId,
                venueId,
                teamName,
                leagueId,
                availableSeason
        );
    }
}
