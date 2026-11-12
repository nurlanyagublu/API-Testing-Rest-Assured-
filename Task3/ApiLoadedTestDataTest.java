package API.Task3;

import io.restassured.RestAssured;
import java.util.List;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import static org.hamcrest.MatcherAssert.assertThat;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ApiLoadedTestDataTest {
    private static FootballTestData testData;

    @BeforeAll
    static void loadTeamDataFromApi() {

        String apiKey = System.getenv("API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("API_KEY environment variable is missing");
        }

        RestAssured.requestSpecification =
                new RequestSpecBuilder()
                        .setBaseUri("https://v3.football.api-sports.io")
                        .setAccept(ContentType.JSON)
                        .addHeader("x-apisports-key", apiKey.trim())
                        .build();

        testData = FootballTestDataLoader.load();
    }

    @Test
    void getSeasonForLoadedTeam() {

        Response response =
                given()
                        .queryParam("team", testData.teamId())
                .when()
                        .get("/teams/seasons")
                .then()
                        .extract()
                        .response();

        int statusCode = response.getStatusCode();
        int results = response.path("results");
        List<Integer> seasons = response.jsonPath().getList("response", Integer.class);

        assertThat(statusCode, equalTo(200));
        assertThat(results, greaterThan(0));
        assertThat(seasons, not(empty()));
        assertThat(seasons, hasItem(testData.availableSeason()));
        assertThat(seasons, everyItem(instanceOf(Integer.class)));
    }

    @Test
    void getStatisticsForLoadedTeam() {

        Response response =
                given()
                        .queryParam("league", testData.leagueId())
                        .queryParam("season", testData.availableSeason())
                        .queryParam("team", testData.teamId())
                .when()
                        .get("/teams/statistics")
                .then()
                        .extract()
                        .response();

        int statusCode = response.getStatusCode();
        int results = response.path("results");

        assertThat(statusCode, equalTo(200));
        assertThat(results, greaterThan(0));

        int returnedTeamId = response.path("response.team.id");
        String returnedTeamName = response.path("response.team.name");
        int playedGames = response.path("response.fixtures.played.total");

        assertThat(returnedTeamId, equalTo(testData.teamId()));
        assertThat(returnedTeamName, equalTo(testData.teamName()));
        assertThat(playedGames, greaterThan(0));
    }

    @Test
    void getLoadedVenue() {

        Response response =
                given()
                        .queryParam("id", testData.venueId())
                .when()
                        .get("/venues")
                .then()
                        .extract()
                        .response();

        int statusCode = response.getStatusCode();
        int results = response.path("results");

        assertThat(statusCode, equalTo(200));
        assertThat(results, equalTo(1));

        int returnedVenueId = response.path("response[0].id");
        String venueName = response.path("response[0].name");
        String venueCity = response.path("response[0].city");
        int venueCapacity = response.path("response[0].capacity");

        assertThat(returnedVenueId, equalTo(testData.venueId()));
        assertThat(venueName, containsString("Old Trafford"));
        assertThat(venueCity, equalToIgnoringCase("Manchester"));
        assertThat(venueCapacity, greaterThan(50_000));
    }

    @Test
    void getStandingsForLoadedLeague() {

        Response response =
                given()
                        .queryParam("league", testData.leagueId())
                        .queryParam("season", testData.availableSeason())
                .when()
                        .get("/standings")
                .then()
                        .extract()
                        .response();

        int statusCode = response.getStatusCode();
        int results = response.path("results");

        assertThat(statusCode, equalTo(200));
        assertThat(results, greaterThan(0));

        int returnedLeagueId = response.path("response[0].league.id");
        int returnedSeason = response.path("response[0].league.season");
        List<String> teamNames = response.path("response[0].league.standings[0].team.name");
        List<Integer> ranks = response.path("response[0].league.standings[0].rank");

        assertThat(returnedLeagueId, equalTo(testData.leagueId()));
        assertThat(returnedSeason, equalTo(testData.availableSeason()));
        assertThat(teamNames, not(empty()));
        assertThat(teamNames, hasItem(testData.teamName()));
        assertThat(ranks, hasItem(1));
        assertThat(ranks, everyItem(greaterThan(0)));
    }

    @Test
    void verifyLoadedTeamInformation() {

        Response response =
                given()
                        .queryParam("id", testData.teamId())
                .when()
                        .get("/teams")
                .then()
                        .extract()
                        .response();

        int statusCode = response.getStatusCode();
        int results = response.path("results");

        assertThat(statusCode, equalTo(200));
        assertThat(results, equalTo(1));

        int returnedTeamId = response.path("response[0].team.id");
        String returnedTeamName = response.path("response[0].team.name");
        int returnedVenueId = response.path("response[0].venue.id");
        boolean nationalTeam = response.path("response[0].team.national");

        assertThat(returnedTeamId, equalTo(testData.teamId()));
        assertThat(returnedTeamName, allOf(equalTo(testData.teamName()), containsString("United")));
        assertThat(returnedVenueId, equalTo(testData.venueId()));
        assertThat(nationalTeam, is(false));
    }
}