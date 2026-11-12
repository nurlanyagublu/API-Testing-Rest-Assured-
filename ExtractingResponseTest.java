package API;

import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ExtractingResponseTest {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://v3.football.api-sports.io";
    }

    @Test
    void getTeamsStatistics() {
        Response response =
                given()
                        .header("Accept", "application/json")
                        .header("x-apisports-key", System.getenv("API_KEY"))
                        .queryParam("league", 39)
                        .queryParam("season", 2023)
                        .queryParam("team", 33)
                .when()
                        .get("/teams/statistics")
                .then()
                        .extract()
                        .response();

        response.prettyPrint();

        int statusCode = response.getStatusCode();
        int results = response.path("results");

        assertThat(statusCode, equalTo(200));
        assertThat(results, greaterThan(0));

        String teamName = response.path("response.team.name");
        int teamId = response.path("response.team.id");
        int playedGames = response.path("response.fixtures.played.total");
        int goals = response.path("response.goals.against.total.home");

        assertThat(teamId, equalTo(33));
        assertThat(teamName, equalTo("Manchester United"));
        assertThat(playedGames, greaterThan(0));
        assertThat(goals, greaterThan(0));
    }

    @Test
    void getManchesterUnitedSeasons() {
        Response response =
                given()
                        .header("Accept", "application/json")
                        .header("x-apisports-key", System.getenv("API_KEY"))
                        .queryParam("team", 33)
                .when()
                        .get("/teams/seasons")
                .then()
                        .extract()
                        .response();

        int statusCode = response.getStatusCode();
        int results = response.path("results");

        assertThat(statusCode, equalTo(200));
        assertThat(results, greaterThan(0));

        List<Integer> seasons = response.path("response");

        assertThat(seasons, not(empty()));
        assertThat(seasons, hasItem(2023));
        assertThat(seasons, everyItem(instanceOf(Integer.class)));

        response.prettyPrint();
    }

    @Test
    void getTeamCountries() {
        Response response =
                given()
                        .header("Accept", "application/json")
                        .header("x-apisports-key", System.getenv("API_KEY"))
                .when()
                        .get("/teams/countries")
                .then()
                        .extract()
                        .response();

        int statusCode = response.getStatusCode();
        int results = response.path("results");

        assertThat(statusCode, equalTo(200));
        assertThat(results, greaterThan(0));

        List<String> countryNames = response.path("response.name");

        assertThat(countryNames, not(empty()));
        assertThat(countryNames, hasItem("England"));
        assertThat(countryNames, everyItem(not(emptyOrNullString())));

        response.prettyPrint();
    }

    @Test
    void getOldTraffordVenue() {
        Response response =
                given()
                        .header("Accept", "application/json")
                        .header("x-apisports-key", System.getenv("API_KEY"))
                        .queryParam("id", 556)
                .when()
                        .get("/venues")
                .then()
                        .extract()
                        .response();

        int statusCode = response.getStatusCode();
        int results = response.path("results");

        assertThat(statusCode, equalTo(200));
        assertThat(results, equalTo(1));

        int venueId = response.path("response[0].id");
        String venueName = response.path("response[0].name");
        String city = response.path("response[0].city");
        int capacity = response.path("response[0].capacity");
        String image = response.path("response[0].image");

        assertThat(venueId, equalTo(556));
        assertThat(venueName, equalTo("Old Trafford"));
        assertThat(city, containsString("Manchester"));
        assertThat(capacity, greaterThan(50_000));
        assertThat(image, startsWith("https://"));

        response.prettyPrint();
    }

    @Test
    void getPremierLeagueStandings() {
        Response response =
                given()
                        .header("Accept", "application/json")
                        .header("x-apisports-key", System.getenv("API_KEY"))
                        .queryParam("league", 39)
                        .queryParam("season", 2023)
                .when()
                        .get("/standings")
                .then()
                        .extract()
                        .response();

        int statusCode = response.getStatusCode();
        int results = response.path("results");

        assertThat(statusCode, equalTo(200));
        assertThat(results, greaterThan(0));

        int leagueId = response.path("response[0].league.id");
        int season = response.path("response[0].league.season");

        List<String> teamNames = response.path("response[0].league.standings[0].team.name");
        List<Integer> ranks = response.path("response[0].league.standings[0].rank");

        assertThat(leagueId, equalTo(39));
        assertThat(season, equalTo(2023));
        assertThat(teamNames, hasSize(20));
        assertThat(teamNames, hasItem("Manchester United"));
        assertThat(ranks, hasItem(1));
        assertThat(ranks, everyItem(greaterThan(0)));

        response.prettyPrint();
    }
}
